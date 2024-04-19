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
    public Map<String, BigInteger> obterChavesPublicas(@RequestParam(name = "token") String token) {

        System.out.println("---------------------------------------");
        System.out.println("Requisição recebida com o token: " + token);

        // Verifica se o token está presente na URL
        if(ObjectUtils.isEmpty(token)) {
            System.out.println("Token não fornecido na URL");
            return null;
        }

        // Verifica se o token já existe no mapa
        if(chavesPublicasMap.containsKey(token)) {
            // Se o token já existir, retorna as chaves públicas previamente geradas
            BigInteger[] chavesArmazenadas = chavesPublicasMap.get(token);
            BigInteger e = chavesArmazenadas[0];
            BigInteger n = chavesArmazenadas[1];
            System.out.println("Chaves públicas recuperadas: (e=" + e + ", n=" + n + ")");
            System.out.println("---------------------------------------");
            Map<String, BigInteger> chavesPublicas = new HashMap<>();
            chavesPublicas.put("e", e);
            chavesPublicas.put("n", n);
            return chavesPublicas;
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

        // Mensagem formatada para as chaves públicas geradas
        System.out.println("Chaves públicas geradas: (e=" + e + ", n=" + n + ")");
        System.out.println("---------------------------------------");

        // Retornar as chaves públicas no formato desejado
        Map<String, BigInteger> chavesPublicas = new HashMap<>();
        chavesPublicas.put("e", e);
        chavesPublicas.put("n", n);
        return chavesPublicas;
    }
}
