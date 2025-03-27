package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.ArrayList;

public class TransformacaoDeDados {

    public static void main(String[] args) {
        extrairDados();
    }

    private static void extrairDados() {
        // Caminho do PDF e onde o CSV será salvo
        String pdfFilePath = "C:/Users/haris/Downloads/Anexo_I.pdf";
        String csvFilePath = "C:\\Users\\haris\\Downloads\\dados_extraidos.csv";

        try {
            // Carregar o documento PDF
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document);
            document.close();

            // Processa o conteúdo extraído e transforma em estrutura tabular
            List<String[]> dadosExtraidos = processarTexto(texto);

            // Salva os dados no formato CSV
            salvarComoCSV(dadosExtraidos, csvFilePath);

            // Compacta o arquivo CSV para ZIP
            compactarCSV(csvFilePath);

            System.out.println("Processo concluído com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String[]> processarTexto(String texto) {
        List<String[]> dados = new ArrayList<>();
        
        // Divide o texto extraído em linhas
        String[] linhas = texto.split("\n");
        for (String linha : linhas) {
            // Ajustar conforme a estrutura do PDF
            String[] colunas = linha.split("\\s+");
            dados.add(colunas);
        }
        
        return dados;
    }

    private static void salvarComoCSV(List<String[]> dados, String caminhoArquivo) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(caminhoArquivo))) {
            writer.writeAll(dados);
            System.out.println("Dados salvos como CSV com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compactarCSV(String caminhoArquivoCSV) {
        String zipFilePath = "Teste_Rafael_Harissis.zip";

        try (FileInputStream fis = new FileInputStream(caminhoArquivoCSV);
             FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            ZipEntry zipEntry = new ZipEntry("dados_extraidos.csv");
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zipOut.write(buffer, 0, length);
            }

            System.out.println("CSV compactado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
