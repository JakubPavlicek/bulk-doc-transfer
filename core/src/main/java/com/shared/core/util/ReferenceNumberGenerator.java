package com.shared.core.util;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ReferenceNumberGenerator {

    private static final String[] CASE_TYPES = { "C", "T", "P" };
    private static final String[] SENATE_NUMBERS = { "1", "2", "3" };
    private static final Map<String, Long> SEQUENTIAL_NUMBERS = new HashMap<>();
    private static final Random RANDOM = ThreadLocalRandom.current();

    private ReferenceNumberGenerator() {
    }

    /// Generates the next reference number in the format:
    /// - Case Type – Randomly chosen from 'C', 'T', 'P'
    /// - Senate Number – Randomly chosen from '1', '2', '3'
    /// - Year – Current year (e.g., 2025)
    /// - Sequential Number – Order number within the case type, senate, and year
    public static String getNextReferenceNumber() {
        // Select a random case type and senate number + current year
        String selectedCaseType = CASE_TYPES[RANDOM.nextInt(0, 3)];
        String selectedSenateNumber = SENATE_NUMBERS[RANDOM.nextInt(0, 3)];
        String currentYear = Year.now().toString();

        // Build the reference number part
        String referenceNumberPart = selectedCaseType + selectedSenateNumber + currentYear;

        // Get the sequential number and save the next one
        Long sequentialNumber = SEQUENTIAL_NUMBERS.getOrDefault(referenceNumberPart, 1L);
        SEQUENTIAL_NUMBERS.put(referenceNumberPart, sequentialNumber + 1);

        // Build the final reference number
        return referenceNumberPart + sequentialNumber;
    }

}
