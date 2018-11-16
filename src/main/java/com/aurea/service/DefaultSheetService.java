package com.aurea.service;

import com.aurea.client.SheetClient;
import com.aurea.dto.AdvancedGroups;
import com.aurea.dto.Assignment;
import com.aurea.dto.CheckInChat;
import com.aurea.dto.ContractorTimeSlots;
import com.aurea.dto.DayActivitiesTime;
import com.aurea.dto.Group;
import com.aurea.model.SheetItem;
import com.aurea.setting.CandidateSettings;
import com.aurea.setting.CrossOverSettings;
import com.aurea.setting.GoogleSettings;
import com.aurea.util.Utils;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultSheetService implements SheetService {

    private static final String RANGE = "!A2:M";
    private static final String VALUES = "values";
    private static final int HOUR_MINUTES = 60;
    private static final String DEVELOPMENT = "Development";
    private static final int FULL_PERCENTAGE = 100;
    public static final int ALLOWED_IDLE_TIME = 1;
    private final transient SheetClient sheetClient;
    private final transient GroupingService groupingService;
    private final transient CandidateSettings candidateSettings;
    private final transient CrossOverSettings settings;
    private final transient CheckInChatService checkInChatService;
    private final transient GoogleSettings googleSettings;

    DefaultSheetService(final SheetClient sheetClient, final GroupingService groupingService,
            final CheckInChatService checkInChatService, final CandidateSettings candidateSettings,
            final CrossOverSettings settings, final GoogleSettings googleSettings) {
        this.sheetClient = sheetClient;
        this.groupingService = groupingService;
        this.checkInChatService = checkInChatService;
        this.candidateSettings = candidateSettings;
        this.settings = settings;
        this.googleSettings = googleSettings;
    }

    @Override
    public void writeContentSheet(final String date) {
        final CheckInChat[] checkInChats = checkInChatService.getCheckInChat(settings.getTeamId(), date, date);
        final List<ValueRange> data = new ArrayList<>();
        final Group[] groupings = groupingService.getGrouping(settings.getTeamId(), date);
        final Set<SheetItem> items = new HashSet<>();
        for (final Group group : groupings) {
            final Assignment assignment = group.getAssignment();
            final long candidateId = assignment.getCandidate().getId();
            final String candidateName = assignment.getCandidate().getPrintableName();
            final String managerName = assignment.getManager().getPrintableName();
            final AdvancedGroups[] advancedGroups = group.getGrouping().getAdvancedGroups();
            Double devTimeSpent = 0d;
            Double totalTime = 0d;
            if (advancedGroups != null && advancedGroups.length > 0) {
                for (final AdvancedGroups advancedGroup : advancedGroups) {
                    totalTime += advancedGroup.getSpentTime();
                    final String sectionName = advancedGroup.getSectionName();
                    if (DEVELOPMENT.equals(sectionName)) {
                        devTimeSpent = advancedGroup.getSpentTime();
                    }
                }
            }
            final Double hours = totalTime / HOUR_MINUTES;
            final String hourPerDay = Utils.convertToYesNo(hours > candidateSettings.getHourPerDay());
            final DayActivitiesTime dayActivitiesTime = group.getDayActivitiesTime();
            final String deepWorkBlock = getDeepWorkBlock(dayActivitiesTime);
            final Double devTimePer = devTimeSpent * FULL_PERCENTAGE / totalTime;
            final String devTime = Utils.convertToYesNo(devTimePer > candidateSettings.getDevTime());
            final boolean checkInChatStatus = Utils.getCheckInChatStatus(checkInChats, assignment.getId());
            final String dailyCic = Utils.convertToYesNo(checkInChatStatus);
            final String intensityFocus = getIntensityAndFocus(group);
            final String checkInChatCompliance = Utils
                    .convertToYesNo(Utils.getCheckInChatCompliant(checkInChats, assignment.getId()));
            final int blockWorkLess = workBlocksLessThan1HourCount(dayActivitiesTime);
            final Double actualFocus = group.getGrouping().getFocusScore();
            final Double actualIntensity = group.getGrouping().getIntensityScore();
            final SheetItem item = SheetItem.builder().candidateId(candidateId).candidateName(candidateName)
                    .managerName(managerName).date(date).hourPerDay(hourPerDay).deepWorkBlock(deepWorkBlock)
                    .devTime(devTime).dailyCic(dailyCic).intensityFocus(intensityFocus).blockWorkLess(blockWorkLess)
                    .checkInChatCompliance(checkInChatCompliance).actualIntensity(actualIntensity)
                    .actualFocus(actualFocus).build();
            items.add(item);
        }
        items.addAll(readSheetItems());
        final List<SheetItem> finalItems = new ArrayList<>(items);
        Collections.sort(finalItems, (o1, o2) -> o1.getCandidateName().compareTo(o2.getCandidateName()));
        clearSheetData(googleSettings.getSheetName() + RANGE + items.size());
        writeSheetData(data, finalItems);
        log.info("Wrote in sheets {} items", data.size());
    }

    private void clearSheetData(final String range) {
        sheetClient.deleteSheetData(range);
    }

    private Set<SheetItem> readSheetItems() {
        final Set<SheetItem> items = new HashSet<>();
        final Optional<BatchGetValuesResponse> readSheetData = sheetClient
                .readSheetData(Arrays.asList(googleSettings.getSheetName()));
        if (readSheetData.isPresent()) {
            final List<ValueRange> valueRanges = readSheetData.get().getValueRanges();
            if (Objects.nonNull(valueRanges) && !valueRanges.isEmpty()) {
                final ValueRange valueRange = valueRanges.get(0);
                final List<List<String>> readValues = (List<List<String>>) valueRange.get(VALUES);
                if (readValues != null) {
                    for (final List<String> rowItem : Iterables.skip(readValues, 1)) {
                        final SheetItem item = SheetItem.builder().candidateId(Long.valueOf(rowItem.get(0)))
                                .candidateName(rowItem.get(1)).managerName(rowItem.get(2)).date(rowItem.get(3))
                                .hourPerDay(rowItem.get(4)).deepWorkBlock(rowItem.get(5)).devTime(rowItem.get(6))
                                .dailyCic(rowItem.get(7)).intensityFocus(rowItem.get(8))
                                .blockWorkLess(Integer.valueOf(rowItem.get(9))).checkInChatCompliance(rowItem.get(10))
                                .actualIntensity(Double.valueOf(rowItem.get(11)))
                                .actualFocus(Double.valueOf(rowItem.get(12))).build();
                        items.add(item);
                    }
                }
            }
        }
        return items;

    }

    private void writeSheetData(final List<ValueRange> data, final List<SheetItem> items) {
        for (int i = 0; i < items.size(); i++) {
            final SheetItem item = items.get(i);
            final ValueRange valueRange = new ValueRange();
            data.add(valueRange.setRange("A" + (i + 2))
                    .setValues(Arrays.asList(Arrays.asList(item.getCandidateId(), item.getCandidateName(),
                            item.getManagerName(), item.getDate(), item.getHourPerDay(), item.getDeepWorkBlock(),
                            item.getDevTime(), item.getDailyCic(), item.getIntensityFocus(), item.getBlockWorkLess(),
                            item.getCheckInChatCompliance(), item.getActualIntensity(), item.getActualFocus()))));
        }
        sheetClient.updateSheetData(data);
    }

    private String getDeepWorkBlock(final DayActivitiesTime dayActivitiesTime) {
        final Map<Integer, List<ContractorTimeSlots>> blocks = getWorkBlocks(dayActivitiesTime);
        long counts = blocks.size();
        for (final List<ContractorTimeSlots> slots : blocks.values()) {
            final long countNull = slots.stream().filter(s -> s == null).count();
            if (countNull > ALLOWED_IDLE_TIME) {
                counts++;
            }
        }
        return Utils.convertToYesNo(counts <= candidateSettings.getDeepWorkBlock());
    }

    private Map<Integer, List<ContractorTimeSlots>> getWorkBlocks(final DayActivitiesTime dayActivitiesTime) {
        final Map<Integer, List<ContractorTimeSlots>> blocks = new HashMap<>();
        if (Objects.nonNull(dayActivitiesTime)) {
            final ContractorTimeSlots[] contractorTimeSlots = dayActivitiesTime.getContractorTimeSlots();
            if (Objects.nonNull(contractorTimeSlots)) {
                calculateTimeSlots(blocks, contractorTimeSlots);
            }
        }
        return getBlocksMap(blocks);
    }

    private void calculateTimeSlots(final Map<Integer, List<ContractorTimeSlots>> blocks,
            final ContractorTimeSlots... contractorTimeSlots) {
        int key = 0;
        List<ContractorTimeSlots> timeSlots = new ArrayList<>();
        for (int j = 0; j < contractorTimeSlots.length; j++) {
            final int k = j + 1;
            if (Objects.isNull(contractorTimeSlots[j]) && k < contractorTimeSlots.length
                    && Objects.isNull(contractorTimeSlots[k])) {
                blocks.put(key++, timeSlots);
                if (!timeSlots.isEmpty() && Objects.isNull(timeSlots.get(0))) {
                    timeSlots.remove(0);
                    timeSlots = new ArrayList<>();
                }
            }
            addTimeSlot(timeSlots, contractorTimeSlots[j]);
        }
    }

    private void addTimeSlot(final List<ContractorTimeSlots> timeSlots, final ContractorTimeSlots contractorTimeSlot) {
        final int size = timeSlots.size();
        if (size == 0 || size > 0 && timeSlots.get(size - 1) != contractorTimeSlot) {
            timeSlots.add(contractorTimeSlot);
        }
    }

    private Map<Integer, List<ContractorTimeSlots>> getBlocksMap(final Map<Integer, List<ContractorTimeSlots>> blocks) {
        final Map<Integer, List<ContractorTimeSlots>> blockMap = new HashMap<>();
        blocks.forEach((key, value) -> {
            if (!value.isEmpty()) {
                blockMap.put(key, value);
            }
        });
        return blockMap;
    }

    private int workBlocksLessThan1HourCount(final DayActivitiesTime dayActivitiesTime) {
        final List<ContractorTimeSlots> timeSlots = new ArrayList<>();
        if (Objects.nonNull(dayActivitiesTime)) {
            final ContractorTimeSlots[] contractorTimeSlots = dayActivitiesTime.getContractorTimeSlots();
            if (Objects.nonNull(contractorTimeSlots)) {
                for (int j = 0; j < contractorTimeSlots.length; j++) {
                    if (j == 0 || timeSlots.get(timeSlots.size() - 1) != contractorTimeSlots[j]) {
                        timeSlots.add(contractorTimeSlots[j]);
                    }
                }
            }
        }
        final Map<Integer, Integer> countMap = getCountMap(timeSlots);
        return getNumberOfBlocks(countMap);
    }

    private int getNumberOfBlocks(final Map<Integer, Integer> countMap) {
        int totalCount = 0;
        for (final Integer keyItem : countMap.keySet()) {
            final Integer valueItem = countMap.get(keyItem);
            if (valueItem > 0 && valueItem < candidateSettings.getWorkBlocksLess()) {
                totalCount++;
            }
        }
        return totalCount;
    }

    private Map<Integer, Integer> getCountMap(final List<ContractorTimeSlots> timeSlots) {
        int count = 0;
        int key = 0;
        final Map<Integer, Integer> countMap = new HashMap<>();
        for (final ContractorTimeSlots timeSlot : timeSlots) {
            if (Objects.nonNull(timeSlot)) {
                count++;
            } else {
                countMap.put(key++, count);
                count = 0;
            }
        }
        return countMap;
    }

    private String getIntensityAndFocus(final Group group) {
        final Double focusScores = group.getGrouping().getFocusScore();
        final Double intensityScores = group.getGrouping().getIntensityScore();
        return Utils.convertToYesNo(focusScores >= candidateSettings.getFocusScore()
                && intensityScores >= candidateSettings.getIntensityScore());
    }

}
