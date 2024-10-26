package com.y_lab.project.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.repository.HabitRepositoryJdbcImpl;
import com.y_lab.project.service.HabitService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

@WebServlet("/api/habits/*")
public class HabitServlet extends HttpServlet {

    private final HabitRepositoryJdbcImpl habitRepository;
    private final HabitService habitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HabitServlet(Connection connection, HabitService habitService) {
        this.habitRepository = new HabitRepositoryJdbcImpl(connection);
        this.habitService = habitService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        User user = getUserFromSession(request); // Получаем пользователя из сессии
        response.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            // Получение всех привычек пользователя
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(habitRepository.findAllByUser(user)));
        } else {
            // Получение привычки по ID
            String habitId = pathInfo.substring(1);
            Optional<Habit> habit = habitRepository.findByIdAndUser(Long.parseLong(habitId), user);

            if (habit.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(habit.get()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Привычка не найдена\"}");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        User user = getUserFromSession(request);

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Необходимо указать ID привычки\"}");
            return;
        }

        String habitId = pathInfo.substring(1);

        try {
            HabitDTO habitDTO = objectMapper.readValue(request.getReader(), HabitDTO.class);
            String updateResult = habitService.updateHabit(user, Long.parseLong(habitId), habitDTO.getName(), habitDTO.getDescription(), habitDTO.getFrequency());

            if (updateResult.equals("Привычка успешно обновлена!")) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"" + updateResult + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"" + updateResult + "\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Ошибка при обновлении привычки\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        User user = getUserFromSession(request);

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Необходимо указать ID привычки\"}");
            return;
        }

        String habitId = pathInfo.substring(1);

        try {
            Optional<Habit> habit = habitRepository.findByIdAndUser(Long.parseLong(habitId), user);
            if (habit.isPresent()) {
                habitRepository.deleteByIdAndUser(Long.parseLong(habitId), user);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"status\":\"Привычка успешно удалена\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Привычка не найдена\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Ошибка при удалении привычки\"}");
        }
    }

    private User getUserFromSession(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }
}
