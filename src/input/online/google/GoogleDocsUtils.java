package input.online.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.Docs.Documents;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.BatchUpdateDocumentRequest;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.docs.v1.model.InsertTextRequest;
import com.google.api.services.docs.v1.model.Location;
import com.google.api.services.docs.v1.model.ParagraphElement;
import com.google.api.services.docs.v1.model.Request;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.docs.v1.model.TextRun;

import logic.data.fileLocators.URLLocator;

public class GoogleDocsUtils {

	private static final String APPLICATION_NAME = "Google Docs API Extract Guide";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	/**
	 * Global instance of the scopes required by this quickstart. If modifying these scopes, delete
	 * your previously saved tokens/ folder.
	 */
	private static final List<String> SCOPES =
			Collections.singletonList(DocsScopes.DOCUMENTS);

	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Creates an authorized Credential object.
	 *
	 * @param HTTP_TRANSPORT The network HTTP Transport.
	 * @return An authorized Credential object.
	 * @throws IOException If the credentials.json file cannot be found.
	 */
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
			throws IOException {
		// Load client secrets.
		InputStream in = ExtractText.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		GoogleClientSecrets clientSecrets =
				GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow =
				new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
				.setAccessType("offline")
				.build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
	
	public static List<StructuralElement> getContentsFromDocument(String documentId) throws 
	IOException,
	GeneralSecurityException {	
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		Docs service =
				new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(documentId)
				.build();
		
		
		Document doc = service.documents().get(documentId).execute();
		
		return doc.getBody().getContent();
	}
	
	public static void printNewParagraphAtEndOfDocumentWithDaylessTimestamp(String documentId, String s) throws GeneralSecurityException, IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String time =  new SimpleDateFormat("dd hh:mm:ss").format(timestamp);
		
		List<Request> requests = new LinkedList<>();
		
		int size = getContentsFromDocument(documentId)
				.stream()
				.map(x->x.getEndIndex())
				.reduce(2,Integer::max)-1;

		requests.add(new Request()
				.setInsertText(
						new InsertTextRequest()
						.setLocation(new Location().setIndex(size))
						.setText("["+time+"]"+s+"\n")));

		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

		BatchUpdateDocumentRequest body = new BatchUpdateDocumentRequest();
		
		
		
		
		Docs service =
				new Docs.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
				.setApplicationName(documentId)
				.build();
		service.documents().batchUpdate(documentId,
		    body.setRequests(requests)).execute();		
	}

	/**
	 * Recurses through a list of Structural Elements to read a document's text where text may be in
	 * nested elements.
	 *
	 * @param elements a list of Structural Elements
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 */
	public static List<String> getAllParagraphs(String documentId) throws IOException, GeneralSecurityException {
		
		List<StructuralElement> elements = getContentsFromDocument(documentId);
		List<String> paragraphs = new LinkedList<>();
	
		for (StructuralElement element : elements) {
			if (element.getParagraph() != null) {
				for (ParagraphElement paragraphElement : element.getParagraph().getElements()) {
					paragraphs.add(GoogleDocsUtils.readParagraphElement(paragraphElement));
				}
			}
		}
		return paragraphs;
	}

	/**
	 * Returns the text in the given ParagraphElement.
	 *
	 * @param element a ParagraphElement from a Google Doc
	 */
	static String readParagraphElement(ParagraphElement element) {
		TextRun run = element.getTextRun();
		if (run == null || run.getContent() == null) {
			// The TextRun can be null if there is an inline object.
			return "";
		}
		String res = run.getContent();
		return res.substring(0,res.length()-1);
	}

	public static String getAllText(URLLocator fl) {
		try {
			return getAllParagraphs(URLLocator.getGoogleDocID(fl)).stream().reduce("", (x,y)->x+"\n"+y);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
			return "connexion broken:"+e.getMessage();
		}
	}

	public static void printNewParagraphAtStartOfDocumentWithTimestamp(URLLocator loc, String x) throws GeneralSecurityException, IOException {
		printNewParagraphAtEndOfDocumentWithDaylessTimestamp(URLLocator.getGoogleDocID(loc), x);
	}

}
