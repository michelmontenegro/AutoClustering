package br.rede.autoclustering.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Util {

	public static int generation=0;
	public static int run=0;
	
    @SuppressWarnings("finally")
    public static String escrever(String endereco, String nomeDoArquivo, StringBuilder builder) {
        String msg = "";
        try {
            File file = new File(endereco + File.separator + nomeDoArquivo);
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw); // BufferedWriter
            bw.write(builder.toString()); // Inserção bufferizada da String texto1 no arquivo file.txt
            bw.flush();
            bw.close();
            msg = "Informações adicionadas com sucesso ao arquivo "+file.getAbsoluteFile();
        } catch (IOException e) {
            msg = "Erro de IO: Falha ao escrever arquivo: " + e.getMessage();
            System.err.println("Falha ao escrever arquivo: " + e.getMessage());
        }finally{
            return msg;
        }
    }
    
    public static void writePRobability(String name, StringBuilder builder) {
        String fileName = name+".csv";
        escrever(System.getProperty("user.dir"), fileName, builder);
    }
	
}
