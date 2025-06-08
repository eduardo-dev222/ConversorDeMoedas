
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class ExchangeResponse {
    double conversion_rate;
}

public class Principal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int tentativas = 0;

        while (tentativas < 3) {
            System.out.println("""
                Olá, seja bem-vindo(a) ao Conversor de Moedas!

                Escolha uma opção:
                1) Dólar => Peso argentino
                2) Peso argentino => Dólar
                3) Dólar => Real brasileiro
                4) Real brasileiro => Dólar
                5) Dólar => Peso colombiano
                6) Peso colombiano => Dólar
                7) Sair
                """);

            System.out.print("Digite sua opção: ");
            int opcao = scanner.nextInt();

            String moedaOrigem = "";
            String moedaDestino = "";

            switch (opcao) {
                case 1 -> { moedaOrigem = "USD"; moedaDestino = "ARS"; }
                case 2 -> { moedaOrigem = "ARS"; moedaDestino = "USD"; }
                case 3 -> { moedaOrigem = "USD"; moedaDestino = "BRL"; }
                case 4 -> { moedaOrigem = "BRL"; moedaDestino = "USD"; }
                case 5 -> { moedaOrigem = "USD"; moedaDestino = "COP"; }
                case 6 -> { moedaOrigem = "COP"; moedaDestino = "USD"; }
                case 7 -> {
                    System.out.println("Saindo da aplicação...");
                    scanner.close();
                    return;
                }
                default -> {
                    tentativas++;
                    System.out.println("Opção inválida. Tentativas restantes: " + (3 - tentativas));
                    continue;
                }
            }

            System.out.print("Digite o valor que deseja converter: ");
            double valor = scanner.nextDouble();

            try {
                String apiKey = "4f2404397927fb051d42d827";
                String urlStr = String.format("https://v6.exchangerate-api.com/v6/%s/pair/%s/%s",
                        apiKey, moedaOrigem, moedaDestino);

                URL url = new URL(urlStr);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");
                conexao.connect();

                int responseCode = conexao.getResponseCode();

                if (responseCode != 200) {
                    throw new RuntimeException("Erro na requisição HTTP: código " + responseCode);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                StringBuilder respostaJson = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    respostaJson.append(linha);
                }
                reader.close();

                // Parse JSON com Gson
                Gson gson = new Gson();
                ExchangeResponse exchange = gson.fromJson(respostaJson.toString(), ExchangeResponse.class);
                double valorConvertido = valor * exchange.conversion_rate;

                System.out.printf("Valor convertido: %.2f %s%n%n", valorConvertido, moedaDestino);

            } catch (Exception e) {
                System.out.println("Erro ao converter moeda: " + e.getMessage());
            }
        }

        System.out.println("Número de tentativas excedido. Encerrando o programa.");
        scanner.close();
    }
}


