package com.example.openCode.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class BufferedRequestWrapper extends HttpServletRequestWrapper {
    private final String body;

    public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        body = stringBuilder.toString();
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(body));
    }

    public String getBody() {
        return body;
    }
}
