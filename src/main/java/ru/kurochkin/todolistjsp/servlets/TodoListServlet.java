package ru.kurochkin.todolistjsp.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.kurochkin.todolistjsp.data.TodoItem;
import ru.kurochkin.todolistjsp.data.TodoItemRepository;
import ru.kurochkin.todolistjsp.data.TodoItemsInMemoryRepository;

import java.io.IOException;
import java.io.Serial;
import java.util.*;

@WebServlet("")
public class TodoListServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setContentType("text/html");

        HttpSession session = req.getSession(false);

        if (session != null) {
            String errorMessage = (String) session.getAttribute("actionErrorText");

            if (errorMessage != null) {
                req.setAttribute("errorMessage", errorMessage);

                session.removeAttribute("actionErrorText");
            }

            String createErrorMessage = (String) session.getAttribute("createErrorText");

            if (createErrorMessage != null) {
                req.setAttribute("createErrorMessage", createErrorMessage);

                session.removeAttribute("createErrorText");
            }

            Integer sessionEditingItemId = (Integer) session.getAttribute("editingItemId");

            if (sessionEditingItemId != null) {
                req.setAttribute("editingItemId", sessionEditingItemId);

                session.removeAttribute("editingItemId");
            }

            String editingItemErrorText = (String) session.getAttribute("editingItemErrorText");

            if (editingItemErrorText != null) {
                req.setAttribute("editingItemErrorText", editingItemErrorText);

                session.removeAttribute("editingItemErrorText");
            }
        }

        TodoItemRepository todoItemRepository = new TodoItemsInMemoryRepository();
        List<TodoItem> todoItems = todoItemRepository.getAll();

        req.setAttribute("todoItems", todoItems);

        req.getRequestDispatcher("/todoList.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "create" -> {
                String text = req.getParameter("text");
                HttpSession session = req.getSession();

                if (text == null) {
                    session.setAttribute("actionErrorText", "Ошибка при добавлении новой записи: не передан обязательный параметр id записи");
                } else {
                    text = text.trim();

                    if (text.isEmpty()) {
                        session.setAttribute("createErrorText", "Необходимо указать текст");
                    } else {
                        TodoItemRepository todoItemRepository = new TodoItemsInMemoryRepository();
                        todoItemRepository.create(new TodoItem(text));
                    }
                }
            }
            case "edit" -> {
                String idString = req.getParameter("id");
                HttpSession session = req.getSession();

                if (idString == null) {
                    session.setAttribute("actionErrorText", "Ошибка при редактировании записи: не передан обязательный параметр id записи");
                } else {
                    try {
                        int id = Integer.parseInt(idString);
                        session.setAttribute("editingItemId", id);
                    } catch (NumberFormatException e) {
                        session.setAttribute("actionErrorText", "Ошибка при редактировании записи: не распознан параметр id записи");
                    }
                }
            }
            case "cancel" -> {
                HttpSession session = req.getSession();

                session.removeAttribute("editingItemId");
            }
            case "save" -> {
                String text = req.getParameter("text");
                String idString = req.getParameter("id");
                HttpSession session = req.getSession();

                if (text == null) {
                    session.setAttribute("actionErrorText", "Ошибка при сохранении записи: не передан обязательный параметр text записи");
                } else if (idString == null) {
                    session.setAttribute("actionErrorText", "Ошибка при сохранении записи: не передан обязательный параметр id записи");
                } else {
                    try {
                        int id = Integer.parseInt(idString);
                        text = text.trim();

                        if (text.isEmpty()) {
                            session.setAttribute("editingItemErrorText", "Необходимо указать текст");
                            session.setAttribute("editingItemId", id);
                        } else {
                            TodoItemRepository todoItemRepository = new TodoItemsInMemoryRepository();
                            todoItemRepository.update(new TodoItem(id, text));
                            session.removeAttribute("editingItemId");
                        }
                    } catch (NumberFormatException e) {
                        session.setAttribute("actionErrorText", "Ошибка при сохранении записи: не распознан параметр id записи");
                    } catch (NoSuchElementException e) {
                        session.setAttribute("actionErrorText", "Ошибка при сохранении записи: " + e.getMessage());
                    }
                }
            }
            case "delete" -> {
                String idString = req.getParameter("id");

                if (idString == null) {
                    HttpSession session = req.getSession();
                    session.setAttribute("actionErrorText", "Ошибка при удалении записи: не передан обязательный параметр id записи");
                } else {
                    try {
                        int id = Integer.parseInt(idString);

                        TodoItemRepository todoItemRepository = new TodoItemsInMemoryRepository();
                        todoItemRepository.delete(id);
                    } catch (NumberFormatException e) {
                        HttpSession session = req.getSession();
                        session.setAttribute("actionErrorText", "Ошибка при удалении записи: не распознан параметр id записи");
                    } catch (NoSuchElementException e) {
                        HttpSession session = req.getSession();
                        session.setAttribute("actionErrorText", "Ошибка при удалении записи: " + e.getMessage());
                    }
                }
            }
            default -> {
                HttpSession session = req.getSession();

                session.setAttribute("actionErrorText", "Системе не удалось определить действие \"%s\"".formatted(action));
            }
        }

        String baseUrl = getServletContext().getContextPath() + "/";
        resp.sendRedirect(baseUrl);
    }
}
