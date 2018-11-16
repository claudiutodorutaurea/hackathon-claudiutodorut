package com.aurea.client;

import com.aurea.setting.GoogleSettings;
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
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ClearValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SheetClient {
    private static final String USER = "user";
    private static final String RAW = "RAW";
    private static final String APPLICATION_NAME = "Hackathon";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS = "tokens";
    private transient Sheets sheetService;
    private final transient GoogleSettings googleSettings;
    /**
     * Global instance of the scopes required by this app. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE = "/credentials.json";

    SheetClient(final GoogleSettings googleSettings) {
        this.googleSettings = googleSettings;
    }

    @PostConstruct
    public void init() {
        try {
            this.sheetService = getSheetService();
        } catch (IOException | GeneralSecurityException e) {
            log.error("Error getting sheet service", e);
        }
    }

    /**
     * Creates an authorized Credential object.
     * 
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        final InputStream inputStream = SheetClient.class.getResourceAsStream(CREDENTIALS_FILE);
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(inputStream));

        // Build flow and trigger user authorization request.
        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, SCOPES).setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS))).build();
        final LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize(USER);
    }

    public Optional<BatchUpdateValuesResponse> updateSheetData(final List<ValueRange> values) {
        final BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest().setValueInputOption(RAW)
                .setData(values);
        try {
            return Optional.ofNullable(sheetService.spreadsheets().values()
                    .batchUpdate(googleSettings.getSpreedsheetId(), batchBody).execute());
        } catch (final IOException e) {
            log.error("Error updating sheet", e);
        }
        return Optional.empty();
    }

    private static Sheets getSheetService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build();
    }

    public Optional<BatchGetValuesResponse> readSheetData(final List<String> ranges) {
        try {
            return Optional.ofNullable(sheetService.spreadsheets().values().batchGet(googleSettings.getSpreedsheetId()).setRanges(ranges)
                    .execute());
        } catch (final IOException e) {
            log.error("Error reading from sheet", e);
        }
        return Optional.empty();
    }

    public ClearValuesResponse deleteSheetData(final String range) {
        try {
            final ClearValuesRequest requestBody = new ClearValuesRequest();
            return sheetService.spreadsheets().values().clear(googleSettings.getSpreedsheetId(), range, requestBody)
                    .execute();
        } catch (final IOException e) {
            log.error("Error delete from sheet", e);
        }
        return null;
    }

    public Optional<AppendValuesResponse> appendSheetData(final ValueRange values, final String range) {
        try {
            return Optional.ofNullable(sheetService.spreadsheets().values().append(googleSettings.getSpreedsheetId(), range, values)
                    .execute());
        } catch (final IOException e) {
            log.error("Error writing in sheet", e);
        }
        return Optional.empty();
    }

}
