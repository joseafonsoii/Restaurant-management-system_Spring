package com.jose.restaurant_management_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseChecker implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseChecker.class);

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection()) {
            log.info("CONEXÃO COM BANCO DE DADOS ESTABELECIDA!");
            log.info("Banco: {}", conn.getMetaData().getDatabaseProductName());
            log.info(" URL: {}", conn.getMetaData().getURL());
            log.info("Usuário: {}", conn.getMetaData().getUserName());

        } catch (Exception e) {
            log.error(" ERRO NA CONEXÃO: {}", e.getMessage());
        }
    }
}