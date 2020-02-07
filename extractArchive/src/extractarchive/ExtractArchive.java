/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractarchive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import org.apache.commons.compress.compressors.gzip.GzipUtils;

/**
 *
 * @author P05144
 */
public class ExtractArchive {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Options options = new Options();
        CommandLine cmd = null;
        String inputFolder =null,outputFolder =null;
        Boolean showHelpMessage = false;
        Boolean showVersion = false;
         try {
            options.addOption( "v", "version", false, "display version" );
            options.addOption( "h", "help", false, "show help" );
            options.addOption(Option.builder("i")
                    .longOpt( "i" )
                    .desc( "Input Folder")
                    .hasArg()
                    .argName( "input" ).build());
            options.addOption(Option.builder("o")
                    .longOpt( "o" )
                    .desc( "Output Folder")
                    .hasArg()
                    .argName( "output" ).build());

            //Parse command line arguments
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse( options, args);
            if( cmd.hasOption("v")){
                showVersion = true;
            }
            if( cmd.hasOption("h")){
                showHelpMessage = true;
            }
            if(cmd.hasOption("i")){                
              inputFolder= cmd.getOptionValue("i").toUpperCase();
            }
            if(cmd.hasOption("o")){              
              outputFolder = cmd.getOptionValue("o").toUpperCase();
            }
            

         }catch (ParseException ex) {
            //System.out.println("ParseException ERROR GENERAL "+ex.getMessage().toString());
           
            System.err.println("ParseException ERROR GENERAL "+ex.getMessage());
            System.exit(1);
        }
        
        
        if(showVersion == true ){
                System.out.println("1.0.0 \n Extract Gzip Copyright (c)  Telefonica VENEZUELA ");
                //System.out.println("1.0.0");
                //System.out.println("Copyright (c)"+executionTime.getDate()+" Telefonica VENEZUELA");
                System.exit(0);
        }
        
        if( showHelpMessage == true || 
                  inputFolder == null || outputFolder ==null
                 ){
                     HelpFormatter formatter = new HelpFormatter();
                     String header = "Extract .GZ \n\n";
                     String footer = "\n";
                     footer += "Examples: \n";
                     footer += "java -jar extractArchive.jar -i INPUT_FOLDER -o OUTPUT_FOLDER\n";
                     formatter.printHelp( "java -jar extractArchive.jar -h", header, options, footer );
                     System.exit(0);
        }
        
        try {
            gzFolder(inputFolder,outputFolder);
        } catch (IOException ex) {
            System.err.println("IOException "+ex.getMessage());
            System.exit(1);
        }

    }
    /**
     * METODO QUE LEE LOS ARCHIVOS .GZ DE UNA CARPETA
     * @param input
     * @param output
     * @throws IOException 
     */
    public static void gzFolder(String input, String output) throws IOException {

        File inputDirectory = new File(input);
        File outputDirectory = new File(output);
        try (Stream<Path> paths = Files.walk(Paths.get(inputDirectory.getAbsolutePath()))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach((t) -> {
                        if (GzipUtils.isCompressedFilename(t.toString())) {
                            try {
                                extractGzip(t, outputDirectory.getAbsolutePath());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
        }
    }
    
    /**
     * Descromprime archivo por archivo de la carpeta
     * @param inputFile
     * @param outputFile
     * @throws IOException 
     */
    public static void extractGzip(Path inputFile, String outputFile) throws IOException {
        System.out.println("Extracting " + inputFile.getFileName().toString() + " ... ");
        InputStream fin = Files.newInputStream(inputFile);
        BufferedInputStream in = new BufferedInputStream(fin);
        GzipCompressorInputStream gzIn;
        try (OutputStream out = Files.newOutputStream(Paths.get(outputFile + File.separator + GzipUtils.getUncompressedFilename(inputFile.getFileName().toString())))) {
            gzIn = new GzipCompressorInputStream(in);
            final byte[] buffer = new byte[2048];
            int n = 0;
            while (-1 != (n = gzIn.read(buffer))) {
                out.write(buffer, 0, n);
            }
        }
        gzIn.close();
        System.out.println("Extract " + inputFile.getFileName().toString() + " DONE");
    }

}
