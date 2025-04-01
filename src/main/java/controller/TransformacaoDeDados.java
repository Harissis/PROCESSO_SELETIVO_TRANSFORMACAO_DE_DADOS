package controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.ArrayList;

public class TransformacaoDeDados {

    public static void main(String[] args) {
        extrairDados();
    }

    private static void extrairDados() {
        // Configuração de caminhos
        String userHome = System.getProperty("user.home");
        String pdfFilePath = userHome + "/Downloads/Anexo_I.pdf";
        String csvFilePath = userHome + "/Downloads/dados_extraidos.csv";
        String zipFilePath = userHome + "/Downloads/Teste_Rafael_Harissis.zip";

        System.out.println("═══════════════════════════════════════════");
        System.out.println("   PROCESSANDO ARQUIVO: " + pdfFilePath);
        System.out.println("═══════════════════════════════════════════");

        try {
            // 1. Extrair texto do PDF
            System.out.println("\n▸ Passo 1/4: Extraindo texto do PDF...");
            PDDocument document = PDDocument.load(new File(pdfFilePath));
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(document.getNumberOfPages());
            String texto = stripper.getText(document);
            document.close();

            // 2. Processar e substituir valores
            System.out.println("▸ Passo 2/4: Processando dados e substituindo OD/AMB...");
            List<String[]> dadosExtraidos = processarTexto(texto);

            // 3. Salvar CSV
            System.out.println("▸ Passo 3/4: Salvando arquivo CSV...");
            salvarComoCSV(dadosExtraidos, csvFilePath);

            // 4. Compactar para ZIP
            System.out.println("▸ Passo 4/4: Criando arquivo ZIP...");
            compactarCSV(csvFilePath, zipFilePath);

            // Relatório final
            System.out.println("\n═══════════════════════════════════════════");
            System.out.println("          PROCESSO CONCLUÍDO!");
            System.out.println("═══════════════════════════════════════════");
            System.out.println("\nARQUIVOS GERADOS:");
            System.out.println("• CSV: " + csvFilePath);
            System.out.println("• ZIP: " + zipFilePath);
            
            System.out.println("\nMODIFICAÇÕES REALIZADAS:");
            System.out.println("✓ Todas as ocorrências de \"OD:\" foram substituídas por \"Seg. Odontológica\"");
            System.out.println("✓ Todas as ocorrências de \"AMB:\" foram substituídas por \"Seg. Ambulatorial\"");
            System.out.println("\nVerificação manual: Abra o arquivo CSV e confirme as substituições");

        } catch (Exception e) {
            System.err.println("\n⚠️ ERRO DURANTE O PROCESSAMENTO:");
            e.printStackTrace();
        }
    }

    private static List<String[]> processarTexto(String texto) {
        List<String[]> dados = new ArrayList<>();
        int substituicoesOD = 0;
        int substituicoesAMB = 0;
        
        String[] linhas = texto.split("\n");
        
        for (String linha : linhas) {
            // Contagem e substituição
            if (linha.contains("\"OD:\"")) {
                substituicoesOD++;
                linha = linha.replace("\"OD:\"", "\"Seg. Odontológica\"");
            }
            if (linha.contains("\"AMB:\"")) {
                substituicoesAMB++;
                linha = linha.replace("\"AMB:\"", "\"Seg. Ambulatorial\"");
            }
            
            // Processamento das colunas
            String[] colunas = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            if (colunas.length > 1) {
                dados.add(colunas);
            }
        }
        
        // Mostra estatísticas de substituição
        System.out.println("\nRELATÓRIO DE SUBSTITUIÇÕES:");
        System.out.println("• \"OD:\" → \"Seg. Odontológica\": " + substituicoesOD + " ocorrências");
        System.out.println("• \"AMB:\" → \"Seg. Ambulatorial\": " + substituicoesAMB + " ocorrências");
        
        return dados;
    }

    private static void salvarComoCSV(List<String[]> dados, String caminhoArquivo) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(caminhoArquivo), 
                CSVWriter.DEFAULT_SEPARATOR, 
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            writer.writeAll(dados);
        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo CSV:");
            e.printStackTrace();
        }
    }

    private static void compactarCSV(String caminhoArquivoCSV, String zipFilePath) {
        try (FileInputStream fis = new FileInputStream(caminhoArquivoCSV);
             FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            zipOut.putNextEntry(new ZipEntry("dados_extraidos.csv"));
            
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }

        } catch (IOException e) {
            System.err.println("Erro ao criar arquivo ZIP:");
            e.printStackTrace();
        }
    }
}
