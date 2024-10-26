package com.y_lab.project.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import com.y_lab.project.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

@WebServlet("/api/users/*")  // Общий эндпоинт для всех запросов к User
public class UserServlet extends HttpServlet {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServlet(Connection connection) {
        this.userRepository = new UserRepository(connection);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            // Получить всех пользователей
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(userRepository.findAll()));
        } else {
            // Получить пользователя по ID
            String userId = pathInfo.substring(1);
            Optional<User> user = userRepository.findById(userId);

            if (user.isPresent()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(user.get()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"ошибка\":\"User не найден\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserDTO userDTO = objectMapper.readValue(request.getReader(), UserDTO.class);

            if (userDTO.getEmail() == null || userDTO.getPassword() == null || userDTO.getName() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"ошибка\":\"Введены неверные данные\"}");
                return;
            }

            User user = new User(userDTO.getEmail(), userDTO.getPassword(), userDTO.getName(), userDTO.isAdmin());
            userRepository.save(user);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(user));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"ошибка\":\"Пользователь не создан\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"ошибка\":\"User ID необходим\"}");
            return;
        }

        String userId = pathInfo.substring(1);

        try {
            UserDTO userDTO = objectMapper.readValue(request.getReader(), UserDTO.class);
            Optional<User> existingUser = userRepository.findById(userId);

            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.updateProfile(userDTO.getName(), userDTO.getEmail());
                userRepository.save(user);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(objectMapper.writeValueAsString(user));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"ошибка\":\"User не найден\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"ошибка\":\"Юзер не обновлён\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"ошибка\":\"User ID не введён\"}");
            return;
        }

        String userId = pathInfo.substring(1);

        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"статус\":\"User успешно удалён\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"статус\":\"User не найден\"}");
        }
    }
}
