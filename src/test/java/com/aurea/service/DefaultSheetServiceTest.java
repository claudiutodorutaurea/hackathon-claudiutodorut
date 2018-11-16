package com.aurea.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aurea.BaseMockitoTest;
import com.aurea.client.SheetClient;
import com.aurea.dto.AdvancedGroups;
import com.aurea.dto.Assignment;
import com.aurea.dto.AssignmentHistory;
import com.aurea.dto.Candidate;
import com.aurea.dto.CheckInChat;
import com.aurea.dto.ContractorTimeSlots;
import com.aurea.dto.DayActivitiesTime;
import com.aurea.dto.Group;
import com.aurea.dto.Grouping;
import com.aurea.dto.Manager;
import com.aurea.setting.CandidateSettings;
import com.aurea.setting.CrossOverSettings;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mock;

public class DefaultSheetServiceTest extends BaseMockitoTest {
    private static final String YES = "Yes";
    private static final String NO = "No";

    private static final String SHEET_NAME = "Index";

    @Mock
    private SheetClient sheetClient;

    @Mock
    private GroupingService groupingService;

    @Mock
    private CandidateSettings candidateSettings;

    @Mock
    private CrossOverSettings settings;

    @Mock
    private CheckInChatService checkInChatService;

    @Test
    public void writeContentSheetCheckInChatNotDone() {
        // Arrange
        final SheetService sheetService = new DefaultSheetService(sheetClient, groupingService, checkInChatService,
                candidateSettings, settings);
        when(candidateSettings.getIntensityScore()).thenReturn(90);
        when(candidateSettings.getFocusScore()).thenReturn(90);
        when(candidateSettings.getDeepWorkBlock()).thenReturn(3);
        when(candidateSettings.getWorkBlocksLess()).thenReturn(6);
        when(candidateSettings.getDevTime()).thenReturn(70);
        final String date = "date";
        final long assignmentId = 10L;
        final CheckInChat[] checkInChats = createCheckingChatNotDone(assignmentId);
        when(checkInChatService.getCheckInChat(settings.getTeamId(), date, date)).thenReturn(checkInChats);
        final Group[] groups = new Group[1];
        groups[0] = new Group();
        final Candidate candidate = createCandidate();
        final Manager manager = createManager();
        final Assignment assignment = createAssignment(assignmentId, candidate, manager);
        groups[0].setAssignment(assignment);
        final AssignmentHistory assignmentHistory = createAssignmentHistory(manager);
        groups[0].setAssignmentHistory(assignmentHistory);
        final DayActivitiesTime dayActivitiesTime = new DayActivitiesTime();
        final ContractorTimeSlots[] contractorTimeSlots = createContractorTimeSlots();
        dayActivitiesTime.setContractorTimeSlots(contractorTimeSlots);
        groups[0].setDayActivitiesTime(dayActivitiesTime);
        final AdvancedGroups[] advancedGroups = createAdvancedGroupsWithDevelopment();
        final Grouping grouping = createGrouping(advancedGroups);
        groups[0].setGrouping(grouping);
        final BatchGetValuesResponse batchRead = new BatchGetValuesResponse();
        final List<ValueRange> valueRanges = Collections.emptyList();
        batchRead.setValueRanges(valueRanges);
        when(sheetClient.readSheetData(Arrays.asList(SHEET_NAME))).thenReturn(Optional.ofNullable(batchRead));
        when(groupingService.getGrouping(settings.getTeamId(), date)).thenReturn(groups);
        final List<List<Object>> values = Arrays.asList(Arrays.asList(candidate.getId(), candidate.getPrintableName(),
                manager.getPrintableName(), date, YES, NO, YES, NO, YES, 6, NO));
        final List<ValueRange> valueRangeList = new ArrayList<>();
        final ValueRange valueRange = new ValueRange();
        valueRange.setRange("A2");
        valueRange.setValues(values);
        valueRangeList.add(valueRange);

        // Act
        sheetService.writeContentSheet(date);

        // Assert
        verify(sheetClient).updateSheetData(valueRangeList);
    }

    @Test
    public void writeContentSheetCheckInChatDone() {
        // Arrange
        final SheetService sheetService = new DefaultSheetService(sheetClient, groupingService, checkInChatService,
                candidateSettings, settings);
        when(candidateSettings.getIntensityScore()).thenReturn(90);
        when(candidateSettings.getFocusScore()).thenReturn(90);
        when(candidateSettings.getDeepWorkBlock()).thenReturn(4);
        when(candidateSettings.getWorkBlocksLess()).thenReturn(6);
        when(candidateSettings.getDevTime()).thenReturn(70);
        final String date = "date";
        final long assignmentId = 10L;
        final CheckInChat[] checkInChats = createCheckingChatDone(assignmentId);
        when(checkInChatService.getCheckInChat(settings.getTeamId(), date, date)).thenReturn(checkInChats);
        final Group[] groups = new Group[1];
        groups[0] = new Group();
        final Candidate candidate = createCandidate();
        final Manager manager = createManager();
        final Assignment assignment = createAssignment(assignmentId, candidate, manager);
        groups[0].setAssignment(assignment);
        final AssignmentHistory assignmentHistory = createAssignmentHistory(manager);
        groups[0].setAssignmentHistory(assignmentHistory);
        final DayActivitiesTime dayActivitiesTime = new DayActivitiesTime();
        final ContractorTimeSlots[] contractorTimeSlots = createContractorTimeSlots();
        dayActivitiesTime.setContractorTimeSlots(contractorTimeSlots);
        groups[0].setDayActivitiesTime(dayActivitiesTime);
        final AdvancedGroups[] advancedGroups = createAdvancedGroupsWithDevelopment();
        final Grouping grouping = createGrouping(advancedGroups);
        groups[0].setGrouping(grouping);
        final BatchGetValuesResponse batchRead = new BatchGetValuesResponse();
        final List<ValueRange> valueRanges = Collections.emptyList();
        batchRead.setValueRanges(valueRanges);
        when(sheetClient.readSheetData(Arrays.asList(SHEET_NAME))).thenReturn(Optional.ofNullable(batchRead));
        when(groupingService.getGrouping(settings.getTeamId(), date)).thenReturn(groups);
        final List<List<Object>> values = Arrays.asList(Arrays.asList(candidate.getId(), candidate.getPrintableName(),
                manager.getPrintableName(), date, YES, YES, YES, YES, YES, 6, YES));
        final List<ValueRange> valueRangeList = new ArrayList<>();
        final ValueRange valueRange = new ValueRange();
        valueRange.setRange("A2");
        valueRange.setValues(values);
        valueRangeList.add(valueRange);

        // Act
        sheetService.writeContentSheet(date);

        // Assert
        verify(sheetClient).updateSheetData(valueRangeList);
    }

    @Test
    public void writeContentSheetNoFocus() {
        // Arrange
        final SheetService sheetService = new DefaultSheetService(sheetClient, groupingService, checkInChatService,
                candidateSettings, settings);
        when(candidateSettings.getFocusScore()).thenReturn(91);
        when(candidateSettings.getDeepWorkBlock()).thenReturn(4);
        when(candidateSettings.getWorkBlocksLess()).thenReturn(6);
        when(candidateSettings.getDevTime()).thenReturn(70);
        final String date = "date";
        final long assignmentId = 10L;
        final CheckInChat[] checkInChats = createCheckingChatDone(assignmentId);
        when(checkInChatService.getCheckInChat(settings.getTeamId(), date, date)).thenReturn(checkInChats);
        final Group[] groups = new Group[1];
        groups[0] = new Group();
        final Candidate candidate = createCandidate();
        final Manager manager = createManager();
        final Assignment assignment = createAssignment(assignmentId, candidate, manager);
        groups[0].setAssignment(assignment);
        final AssignmentHistory assignmentHistory = createAssignmentHistory(manager);
        groups[0].setAssignmentHistory(assignmentHistory);
        final DayActivitiesTime dayActivitiesTime = new DayActivitiesTime();
        final ContractorTimeSlots[] contractorTimeSlots = createContractorTimeSlots();
        dayActivitiesTime.setContractorTimeSlots(contractorTimeSlots);
        groups[0].setDayActivitiesTime(dayActivitiesTime);
        final AdvancedGroups[] advancedGroups = createAdvancedGroupsWithDevelopment();
        final Grouping grouping = createGrouping(advancedGroups);
        groups[0].setGrouping(grouping);
        final BatchGetValuesResponse batchRead = new BatchGetValuesResponse();
        final List<ValueRange> valueRanges = Collections.emptyList();
        batchRead.setValueRanges(valueRanges);
        when(sheetClient.readSheetData(Arrays.asList(SHEET_NAME))).thenReturn(Optional.ofNullable(batchRead));
        when(groupingService.getGrouping(settings.getTeamId(), date)).thenReturn(groups);
        final List<List<Object>> values = Arrays.asList(Arrays.asList(candidate.getId(), candidate.getPrintableName(),
                manager.getPrintableName(), date, YES, YES, YES, YES, NO, 6, YES));
        final List<ValueRange> valueRangeList = new ArrayList<>();
        final ValueRange valueRange = new ValueRange();
        valueRange.setRange("A2");
        valueRange.setValues(values);
        valueRangeList.add(valueRange);

        // Act
        sheetService.writeContentSheet(date);

        // Assert
        verify(sheetClient).updateSheetData(valueRangeList);
    }

    @Test
    public void writeContentSheetNoIntensity() {
        // Arrange
        final SheetService sheetService = new DefaultSheetService(sheetClient, groupingService, checkInChatService,
                candidateSettings, settings);
        when(candidateSettings.getIntensityScore()).thenReturn(91);
        when(candidateSettings.getFocusScore()).thenReturn(90);
        when(candidateSettings.getDeepWorkBlock()).thenReturn(4);
        when(candidateSettings.getWorkBlocksLess()).thenReturn(6);
        when(candidateSettings.getDevTime()).thenReturn(70);
        final String date = "date";
        final long assignmentId = 10L;
        final CheckInChat[] checkInChats = createCheckingChatDone(assignmentId);
        when(checkInChatService.getCheckInChat(settings.getTeamId(), date, date)).thenReturn(checkInChats);
        final Group[] groups = new Group[1];
        groups[0] = new Group();
        final Candidate candidate = createCandidate();
        final Manager manager = createManager();
        final Assignment assignment = createAssignment(assignmentId, candidate, manager);
        groups[0].setAssignment(assignment);
        final AssignmentHistory assignmentHistory = createAssignmentHistory(manager);
        groups[0].setAssignmentHistory(assignmentHistory);
        final DayActivitiesTime dayActivitiesTime = new DayActivitiesTime();
        final ContractorTimeSlots[] contractorTimeSlots = createContractorTimeSlots();
        dayActivitiesTime.setContractorTimeSlots(contractorTimeSlots);
        groups[0].setDayActivitiesTime(dayActivitiesTime);
        final AdvancedGroups[] advancedGroups = createAdvancedGroupsWithDevelopment();
        final Grouping grouping = createGrouping(advancedGroups);
        groups[0].setGrouping(grouping);
        final BatchGetValuesResponse batchRead = new BatchGetValuesResponse();
        final List<ValueRange> valueRanges = Collections.emptyList();
        batchRead.setValueRanges(valueRanges);
        when(sheetClient.readSheetData(Arrays.asList(SHEET_NAME))).thenReturn(Optional.ofNullable(batchRead));
        when(groupingService.getGrouping(settings.getTeamId(), date)).thenReturn(groups);
        final List<List<Object>> values = Arrays.asList(Arrays.asList(candidate.getId(), candidate.getPrintableName(),
                manager.getPrintableName(), date, YES, YES, YES, YES, NO, 6, YES));
        final List<ValueRange> valueRangeList = new ArrayList<>();
        final ValueRange valueRange = new ValueRange();
        valueRange.setRange("A2");
        valueRange.setValues(values);
        valueRangeList.add(valueRange);

        // Act
        sheetService.writeContentSheet(date);

        // Assert
        verify(sheetClient).updateSheetData(valueRangeList);
    }
    
    @Test
    public void writeContentSheetNoDevelopment() {
        // Arrange
        final SheetService sheetService = new DefaultSheetService(sheetClient, groupingService, checkInChatService,
                candidateSettings, settings);
        when(candidateSettings.getIntensityScore()).thenReturn(90);
        when(candidateSettings.getFocusScore()).thenReturn(90);
        when(candidateSettings.getDeepWorkBlock()).thenReturn(4);
        when(candidateSettings.getWorkBlocksLess()).thenReturn(6);
        when(candidateSettings.getDevTime()).thenReturn(70);
        final String date = "date";
        final long assignmentId = 10L;
        final CheckInChat[] checkInChats = createCheckingChatDone(assignmentId);
        when(checkInChatService.getCheckInChat(settings.getTeamId(), date, date)).thenReturn(checkInChats);
        final Group[] groups = new Group[1];
        groups[0] = new Group();
        final Candidate candidate = createCandidate();
        final Manager manager = createManager();
        final Assignment assignment = createAssignment(assignmentId, candidate, manager);
        groups[0].setAssignment(assignment);
        final AssignmentHistory assignmentHistory = createAssignmentHistory(manager);
        groups[0].setAssignmentHistory(assignmentHistory);
        final DayActivitiesTime dayActivitiesTime = new DayActivitiesTime();
        final ContractorTimeSlots[] contractorTimeSlots = createContractorTimeSlots();
        dayActivitiesTime.setContractorTimeSlots(contractorTimeSlots);
        groups[0].setDayActivitiesTime(dayActivitiesTime);
        final AdvancedGroups[] advancedGroups = createAdvancedGroupsNoDevelopment();
        final Grouping grouping = createGrouping(advancedGroups);
        groups[0].setGrouping(grouping);
        final BatchGetValuesResponse batchRead = new BatchGetValuesResponse();
        final List<ValueRange> valueRanges = Collections.emptyList();
        batchRead.setValueRanges(valueRanges);
        when(sheetClient.readSheetData(Arrays.asList(SHEET_NAME))).thenReturn(Optional.ofNullable(batchRead));
        when(groupingService.getGrouping(settings.getTeamId(), date)).thenReturn(groups);
        final List<List<Object>> values = Arrays.asList(Arrays.asList(candidate.getId(), candidate.getPrintableName(),
                manager.getPrintableName(), date, YES, YES, NO, YES, YES, 6, YES));
        final List<ValueRange> valueRangeList = new ArrayList<>();
        final ValueRange valueRange = new ValueRange();
        valueRange.setRange("A2");
        valueRange.setValues(values);
        valueRangeList.add(valueRange);

        // Act
        sheetService.writeContentSheet(date);

        // Assert
        verify(sheetClient).updateSheetData(valueRangeList);
    }

    private CheckInChat[] createCheckingChatDone(final long assignmentId) {
        final CheckInChat[] checkInChats = new CheckInChat[1];
        checkInChats[0] = new CheckInChat();
        checkInChats[0].setAssignmentId(assignmentId);
        checkInChats[0].setStatus("ON_TRACK");
        checkInChats[0].setComment("...yesterday ...today ...blockers");
        return checkInChats;
    }

    private Grouping createGrouping(final AdvancedGroups[] advancedGroups) {
        final Grouping grouping = new Grouping();
        grouping.setFocusScore(90d);
        grouping.setIntensityScore(90d);
        grouping.setAdvancedGroups(advancedGroups);
        return grouping;
    }

    private AssignmentHistory createAssignmentHistory(final Manager manager) {
        final AssignmentHistory assignmentHistory = new AssignmentHistory();
        assignmentHistory.setManager(manager);
        return assignmentHistory;
    }

    private Assignment createAssignment(final long assignmentId, final Candidate candidate, final Manager manager) {
        final Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setCandidate(candidate);
        assignment.setManager(manager);
        return assignment;
    }

    private AdvancedGroups[] createAdvancedGroupsWithDevelopment() {
        final AdvancedGroups[] advancedGroups = new AdvancedGroups[1];
        advancedGroups[0] = new AdvancedGroups();
        advancedGroups[0].setSectionName("Development");
        advancedGroups[0].setSpentTime(400d);
        return advancedGroups;
    }
    
    private AdvancedGroups[] createAdvancedGroupsNoDevelopment() {
        final AdvancedGroups[] advancedGroups = new AdvancedGroups[1];
        advancedGroups[0] = new AdvancedGroups();
        advancedGroups[0].setSectionName("Office");
        advancedGroups[0].setSpentTime(400d);
        return advancedGroups;
    }

    private ContractorTimeSlots[] createContractorTimeSlots() {
        final ContractorTimeSlots[] contractorTimeSlots = new ContractorTimeSlots[20];
        contractorTimeSlots[0] = null;
        contractorTimeSlots[1] = null;
        contractorTimeSlots[2] = null;
        contractorTimeSlots[3] = new ContractorTimeSlots();
        contractorTimeSlots[3].setIndex(3);
        contractorTimeSlots[4] = null;
        contractorTimeSlots[5] = null;
        contractorTimeSlots[6] = new ContractorTimeSlots();
        contractorTimeSlots[6].setIndex(6);
        contractorTimeSlots[7] = new ContractorTimeSlots();
        contractorTimeSlots[7].setIndex(7);
        contractorTimeSlots[8] = null;
        contractorTimeSlots[9] = new ContractorTimeSlots();
        contractorTimeSlots[9].setIndex(9);
        contractorTimeSlots[10] = null;
        contractorTimeSlots[11] = null;
        contractorTimeSlots[12] = new ContractorTimeSlots();
        contractorTimeSlots[12].setIndex(12);
        contractorTimeSlots[13] = new ContractorTimeSlots();
        contractorTimeSlots[13].setIndex(13);
        contractorTimeSlots[14] = null;
        contractorTimeSlots[15] = new ContractorTimeSlots();
        contractorTimeSlots[15].setIndex(15);
        contractorTimeSlots[16] = null;
        contractorTimeSlots[17] = new ContractorTimeSlots();
        contractorTimeSlots[17].setIndex(17);
        contractorTimeSlots[18] = null;
        contractorTimeSlots[19] = null;
        return contractorTimeSlots;
    }

    private Manager createManager() {
        final Manager manager = new Manager();
        final String nameManager = "printableNameManager";
        manager.setPrintableName(nameManager);
        return manager;
    }

    private Candidate createCandidate() {
        final Candidate candidate = new Candidate();
        candidate.setId(1L);
        final String nameCandidate = "printableNameCandidate";
        candidate.setPrintableName(nameCandidate);
        return candidate;
    }

    private CheckInChat[] createCheckingChatNotDone(final long assignmentId) {
        final CheckInChat[] checkInChats = new CheckInChat[1];
        checkInChats[0] = new CheckInChat();
        checkInChats[0].setAssignmentId(assignmentId);
        checkInChats[0].setStatus("Not_Done");
        return checkInChats;
    }
}
