const list = document.querySelector("#task-list");
const form = document.querySelector("#task-form");
const input = document.querySelector("#task-title");
const count = document.querySelector("#task-count");
const progress = document.querySelector("#task-progress");
const message = document.querySelector("#message");

async function request(url, options = {}) {
    const response = await fetch(url, {
        headers: { "Content-Type": "application/json" },
        ...options
    });
    if (!response.ok) {
        const body = await response.json().catch(() => ({}));
        throw new Error(body.error || `Request failed (${response.status})`);
    }
    return response.status === 204 ? null : response.json();
}

function render(tasks) {
    list.replaceChildren();
    const completed = tasks.filter(task => task.completed).length;
    count.textContent = `${tasks.length} task${tasks.length === 1 ? "" : "s"}`;
    progress.textContent = `${completed} completed`;

    tasks.forEach(task => {
        const item = document.createElement("li");
        if (task.completed) item.classList.add("completed");

        const title = document.createElement("span");
        title.className = "title";
        title.textContent = task.title;

        const toggle = document.createElement("button");
        toggle.className = "small-button";
        toggle.textContent = task.completed ? "Undo" : "Complete";
        toggle.addEventListener("click", () => runAction(
            () => request(`/api/tasks/${task.id}/toggle`, { method: "PATCH" })
        ));

        const remove = document.createElement("button");
        remove.className = "small-button delete";
        remove.textContent = "Delete";
        remove.addEventListener("click", () => runAction(
            () => request(`/api/tasks/${task.id}`, { method: "DELETE" })
        ));

        item.append(title, toggle, remove);
        list.append(item);
    });
}

async function loadTasks() {
    try {
        render(await request("/api/tasks"));
        message.textContent = "";
    } catch (error) {
        message.textContent = error.message;
    }
}

async function runAction(action) {
    try {
        await action();
        await loadTasks();
    } catch (error) {
        message.textContent = error.message;
    }
}

form.addEventListener("submit", async event => {
    event.preventDefault();
    const title = input.value.trim();
    if (!title) return;
    await runAction(() => request("/api/tasks", {
        method: "POST",
        body: JSON.stringify({ title })
    }));
    input.value = "";
    input.focus();
});

loadTasks();
