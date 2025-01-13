package ru.sber.model;

public enum TransactionType {
    OPERATION_1("Операция №1", 1),
    OPERATION_2("Операция №2", 2),
    OPERATION_3("Операция №3", 3)
    ;

    private final String name;
    private final Integer partitionNumber;

    private TransactionType(String name, Integer partitionNumber) {
        this.name = name;
        this.partitionNumber = partitionNumber;
    }

    public String getName() {
        return name;
    }

    public Integer getPartitionNumber() {
        return partitionNumber;
    }
}
