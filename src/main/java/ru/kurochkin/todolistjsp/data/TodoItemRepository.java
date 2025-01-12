package ru.kurochkin.todolistjsp.data;

import java.util.List;

public interface TodoItemRepository {
    List<TodoItem> getAll();

    void create(TodoItem item);

    void update(TodoItem item);

    void delete(int id);
}