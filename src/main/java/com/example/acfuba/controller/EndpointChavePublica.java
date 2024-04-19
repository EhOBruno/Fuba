package com.example.acfuba.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.ObjectUtils;

import com.example.acfuba.crypto.CalculaChavePublica;
import com.example.acfuba.util.GerarPrimoAleatorio;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class EndpointChavePublica {

    // Mapa para armazenar o token e as chaves públicas geradas
    private static Map<String, BigInteger[]> chavesPublicasMap = new HashMap<>();

    public static void main(String[] args) {
        SpringApplication.run(EndpointChavePublica.class, args);
    }

    @GetMapping("/chavesPublicas")
    public String obterChavesPublicas(@RequestParam(name = "token") String token) {

        System.out.println(token);

        // Verifica se o token está presente na URL (Normalmente nem chega nessa validação caso não tenha token)
        if(ObjectUtils.isEmpty(token)) {
            return "Token não fornecido na URL";
        }

        // Verifica se o token já existe no mapa
        if(chavesPublicasMap.containsKey(token)) {
            // Se o token já existir, retorna as chaves públicas previamente geradas
            BigInteger[] chavesArmazenadas = chavesPublicasMap.get(token);
            BigInteger e = chavesArmazenadas[0];
            BigInteger n = chavesArmazenadas[1];
            return "Chaves públicas (recuperadas): (" + e + ", " + n + ")";
        }

        int numDigitos = 10; // Número de dígitos para cada número primo

        // Gerar os números primos
        BigInteger p = GerarPrimoAleatorio.gerarPrimoAleatorio(numDigitos);
        BigInteger q = GerarPrimoAleatorio.gerarPrimoAleatorio(numDigitos);

        // Calcular n (produto de p e q)
        BigInteger n = CalculaChavePublica.calcularN(p, q);

        // Calcular fi(n)
        BigInteger fiN = CalculaChavePublica.calcularFiN(p, q);

        // Calcular a segunda chave pública "e"
        BigInteger e = CalculaChavePublica.calcularChavePublicaE(fiN);

        // Armazenar as chaves públicas no mapa
        BigInteger[] chaves = {e, n};
        chavesPublicasMap.put(token, chaves);

        // Retornar as chaves públicas no formato desejado
        return "Chaves públicas (geradas): (" + e + ", " + n + ")";
    }
}
