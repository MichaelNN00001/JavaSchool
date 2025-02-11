package ru.sber.model;

public enum TransactionType {
    OPERATION_1("Операция №1", 1),
    OPERATION_2("Операция №2", 2),
    OPERATION_3("Операция №3", 3)
    ;

    private final String description;
    private final Integer partitionNumber;

    private TransactionType(String description, Integer partitionNumber) {
        this.description = description;
        this.partitionNumber = partitionNumber;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPartitionNumber() {
        return partitionNumber;
    }
}
