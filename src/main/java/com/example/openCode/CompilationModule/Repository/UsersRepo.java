package com.example.openCode.CompilationModule.Repository;

import com.example.openCode.CompilationModule.Model.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    public Users findByUsername(String username);
}
