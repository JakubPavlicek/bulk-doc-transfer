package com.shared.core.util;

import java.time.Year;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenceNumberGenerator {

    private static final String[] CASE_TYPES = { "C", "T", "P" };
    private static final String[] SENATE_NUMBERS = { "1", "2", "3" };
    private static final Map<String, AtomicLong> SEQUENTIAL_NUMBERS = new ConcurrentHashMap<>();

    private ReferenceNumberGenerator() {
    }

    /**
     * Generates the next reference number in the format:
     * <ul>
     *   <li>Case Type – Randomly chosen from 'C', 'T', 'P'</li>
     *   <li>Senate Number – Randomly chosen from '1', '2', '3'</li>
     *   <li>Year – Current year (e.g., 2025)</li>
     *   <li>Sequential Number – Order number within the case type, senate, and year</li>
     * </ul>
     */
    public static String getNextReferenceNumber() {
        // Select a random case type and senate number + current year
        String selectedCaseType = CASE_TYPES[ThreadLocalRandom.current().nextInt(0, 3)];
        String selectedSenateNumber = SENATE_NUMBERS[ThreadLocalRandom.current().nextInt(0, 3)];
        String currentYear = Year.now().toString();

        // Build the reference number part
        String referenceNumberPart = selectedCaseType + selectedSenateNumber + currentYear;

        // Get the sequential number and save the next one
        AtomicLong sequentialNumber = SEQUENTIAL_NUMBERS.computeIfAbsent(referenceNumberPart, k -> new AtomicLong(1));
        long number = sequentialNumber.getAndIncrement();

        // Build the final reference number
        return referenceNumberPart + number;
    }

}
