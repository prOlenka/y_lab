package com.y_lab.project.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.Habit;
import com.y_lab.project.entity.User;
import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.mapper.UserMapper;
import com.y_lab.project.mapper.UserMapperImpl;
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
        UserMapper userMapper = new UserMapperImpl();
        UserDTO userDTO = userMapper.toUserDTO(getUserFromSession(request));
        response.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(habitRepository.findAllByUser(userDTO)));
        } else {
            String habitId = pathInfo.substring(1);
            Optional<Habit> habit = habitRepository.findByIdAndUser(Long.parseLong(habitId), userDTO);

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
        UserMapper userMapper = new UserMapperImpl();
        UserDTO userDTO = userMapper.toUserDTO(getUserFromSession(request));

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Необходимо указать ID привычки\"}");
            return;
        }

        String habitId = pathInfo.substring(1);

        try {
            HabitDTO habitDTO = objectMapper.readValue(request.getReader(), HabitDTO.class);
            String updateResult = habitService.updateHabit(userDTO, Long.parseLong(habitId), habitDTO);

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
        UserMapper userMapper = new UserMapperImpl();
        UserDTO userDTO = userMapper.toUserDTO(getUserFromSession(request));

        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Необходимо указать ID привычки\"}");
            return;
        }

        String habitId = pathInfo.substring(1);

        try {
            Optional<Habit> habit = habitRepository.findByIdAndUser(Long.parseLong(habitId), userDTO);
            if (habit.isPresent()) {
                habitRepository.deleteByIdAndUser(Long.parseLong(habitId), userDTO);
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
