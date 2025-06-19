package in.connectwithsandeepan.marathon.batch;

import in.connectwithsandeepan.marathon.dto.CheckpointRequestDTO;
import in.connectwithsandeepan.marathon.dto.ResultRequestDTO;
import in.connectwithsandeepan.marathon.exception.ExcelReadingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@StepScope
@Slf4j
public class ResultItemReader implements ItemReader<ResultRequestDTO> {

    private final String filePath;
    private final Long eventId;

    private Workbook workbook;
    private Iterator<Row> rowIterator;
    private boolean initialized = false;

    private static final int COL_BIB_NUMBER = 0;
    private static final int COL_PARTICIPANT_NAME = 1;
    private static final int COL_GENDER = 2;
    private static final int COL_RACE_CATEGORY = 3;
    private static final int COL_AGE_CATEGORY = 4;
    private static final int COL_OVERALL_RANK = 5;
    private static final int COL_GENDER_RANK = 6;
    private static final int COL_AGE_CATEGORY_RANK = 7;
    private static final int COL_CHIP_TIME = 8;
    private static final int COL_GUN_TIME = 9;
    private static final int CHECKPOINT_START_COL = 10;

    public ResultItemReader(
            @Value("#{jobParameters['filePath']}") String filePath,
            @Value("#{jobParameters['eventId']}") Long eventId) {
        this.filePath = filePath;
        this.eventId = eventId;
    }

    @Override
    public ResultRequestDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!initialized) {
            initializeReader();
            initialized = true;
        }
        if (rowIterator != null && rowIterator.hasNext()) {
            Row row = rowIterator.next();
            return mapRowToDTO(row);
        }
        closeWorkbook();
        return null;
    }


    private void initializeReader() throws IOException {
        log.info("Opening Excel file: {} for event: {}", filePath, eventId);

        FileInputStream file = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        rowIterator = sheet.iterator();

        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next(); // Skip headers
            log.info("Skipped header row");
        }
    }


    private ResultRequestDTO mapRowToDTO(Row row) {
        try {
            ResultRequestDTO dto = new ResultRequestDTO();

            dto.setBibNumber(getCellValueAsString(row.getCell(COL_BIB_NUMBER)));
            dto.setParticipantName(getCellValueAsString(row.getCell(COL_PARTICIPANT_NAME)));
            dto.setGender(getCellValueAsString(row.getCell(COL_GENDER)));
            dto.setRaceCategory(getCellValueAsString(row.getCell(COL_RACE_CATEGORY)));
            dto.setAgeCategory(getCellValueAsString(row.getCell(COL_AGE_CATEGORY)));
            dto.setOverAllRank(getCellValueAsString(row.getCell(COL_OVERALL_RANK)));
            dto.setGenderRank(getCellValueAsString(row.getCell(COL_GENDER_RANK)));
            dto.setAgeCategoryRank(getCellValueAsString(row.getCell(COL_AGE_CATEGORY_RANK)));
            dto.setChipTime(parseTime(getCellValueAsString(row.getCell(COL_CHIP_TIME))));
            dto.setGunTime(parseTime(getCellValueAsString(row.getCell(COL_GUN_TIME))));
            dto.setCheckpointTimes(mapCheckpoints(row));

            return dto;

        } catch (Exception e) {
            log.error("Error mapping row {} to DTO: {}", row.getRowNum(), e.getMessage());
            throw new ExcelReadingException("Error processing row " + (row.getRowNum() + 1) + ": " + e.getMessage());
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }

        try {
            timeStr = timeStr.trim();
            if (timeStr.matches("\\d{1,2}:\\d{2}:\\d{2}")) {
                return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm:ss"));
            }
            if (timeStr.matches("\\d{1,2}:\\d{2}")) {
                return LocalTime.parse("0:" + timeStr, DateTimeFormatter.ofPattern("H:mm:ss"));
            }
        } catch (Exception e) {
            log.warn("Could not parse time: {}", timeStr);
        }
        return null;
    }

    private List<CheckpointRequestDTO> mapCheckpoints(Row row) {
        List<CheckpointRequestDTO> checkpoints = new ArrayList<>();
        int lastCellNum = row.getLastCellNum();

        for (int i = CHECKPOINT_START_COL; i < lastCellNum; i++) {
            String timeValue = getCellValueAsString(row.getCell(i));

            if (timeValue != null && !timeValue.trim().isEmpty()) {
                LocalTime checkpointTime = parseTime(timeValue);

                if (checkpointTime != null) {
                    CheckpointRequestDTO checkpoint = new CheckpointRequestDTO();
                    checkpoint.setCheckpointNumber(i - CHECKPOINT_START_COL + 1); // Sequential checkpoint number
                    checkpoint.setTime(checkpointTime);
                    checkpoints.add(checkpoint);
                }
            }
        }

        return checkpoints;
    }


    private void closeWorkbook() {
        if (workbook != null) {
            try {
                workbook.close();
                log.info("Closed Excel workbook for file: {}", filePath);
            } catch (IOException e) {
                log.warn("Error closing workbook: {}", e.getMessage());
            }
        }
    }
}
