package com.AiRag.IndianContiutionRag;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {
    private final VectorStore vectorStore;
    private final JdbcClient jdbcClient;

    @Value("classpath:indianConstitution.pdf")
    private Resource pdfResource;
    
    String filePath = "D:\\AI\\IndianContiutionRag\\src\\main\\resources\\indianConstitution.pdf";

    public DataLoader(VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("select COUNT(*) from vector_store").query(Integer.class).single();
        System.out.println("No. of records in DB = " + count);

        if (count == 0) {
            System.out.println("Loading File into PG Vector DB....");

            try {
               
            	// Configure PDF Reader
        		PdfDocumentReaderConfig pdfConfig = PdfDocumentReaderConfig.builder()
        												.withPagesPerDocument(1)
        													.build();
                // Load PDF file from classpath (ensure the file is in src/main/resources)
                ClassPathResource pdfResource = new ClassPathResource("indianConstitution.pdf");
                
                // Create a PagePdfDocumentReader instance
                PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource, pdfConfig);
                

                // Split the text and store it in the vector store
                var textSplitter = new TokenTextSplitter();
                vectorStore.accept(textSplitter.apply(reader.get()));

                System.out.println("Application is ready to serve the request.");
            } catch (Exception e) {
                throw new RuntimeException("Failed to load PDF file", e);
            }
        }
    }
}