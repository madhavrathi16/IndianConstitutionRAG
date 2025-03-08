package RestApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class ICRestController {
	
	private final ChatModel chatModel;
	private final VectorStore vectorStore;
	
	public ICRestController(ChatModel chatModel, VectorStore vectorStore) {
		this.chatModel = chatModel;
		this.vectorStore = vectorStore;
	}
	
	private String prompt =  """
            Your task is to answer the questions about Indian Constitution. Use the information from the DOCUMENTS
            section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
                        
            QUESTION:
            {input}
                        
            DOCUMENTS:
            {documents}
                        
            """;
	
	@GetMapping("/question")
	public String simplify(@RequestParam(value = "question", defaultValue = "What is the Indian Constitution?") String strQuestion) {
		System.out.println("Received question: " + strQuestion);
		
		// Retrieve similar documents
		String documents = findSimilarData(strQuestion);
		
		// Check if we have valid documents
		if (documents.equals("No relevant information found.")) {
			return "I don't have enough information to answer that.";
		}

		// Create prompt template
		PromptTemplate template = new PromptTemplate(prompt);
		Map<String, Object> promptParam = new HashMap<>();
		promptParam.put("input", strQuestion);
		promptParam.put("documents", documents);
		
		// Ensure chatModel is initialized
		if (chatModel == null) {
			return "Error: ChatModel is not initialized.";
		}
//		
//			
//		ChatResponse response = chatModel.call(template.create(promptParam)); // Get the response
//		
//		Generation xyz =response.getResult();
//		
//		 AssistantMessage zz = xyz.getOutput();
//		 zz.getText();
		
		 String response = chatModel.call(template.create(promptParam)).getResult().getOutput().getText();
		
		return response;
	}
	
	private String findSimilarData(String strQuestion) {
		System.out.println("Searching for similar documents...");

        // Correct way to create a SearchRequest
        SearchRequest searchRequest = SearchRequest.builder()
                .query(strQuestion)
                .topK(3)
                .build();

		// Perform similarity search in the VectorStore
		List<Document> docs = vectorStore.similaritySearch(searchRequest);
		
		// Handle empty results
		if (docs == null || docs.isEmpty()) {
			return "No relevant information found.";
		}

		// Join the content of the documents into a single string
		return docs.stream()
                .map(Document::getText) 
                .collect(Collectors.joining("\n\n"));
	}        
}	
