package ru.otus.department;

import org.junit.Test;
import ru.otus.atm.AbstractTest;
import ru.otus.atm.Atm;
import ru.otus.atm.cash.Cash;
import ru.otus.atm.cashissuing.MinimumBanknotesQuantity;
import ru.otus.department.command.TotalBalanceCommand;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DepartmentTest extends AbstractTest {
    private final Department department = new Department(new TotalBalanceCommand());

    @Test
    public void addOneAtmAndReturnQuantity() {
        department.addAtm(atm);
        assertEquals(1, department.getAtmQuantity());
    }

    @Test
    public void returnTotalBalanceForEmptyDepartment() {
        assertEquals(0, department.getTotalBalance());
    }

    @Test
    public void addOneAtmAndReturnTotalBalance() {
        atm.putCash(new Cash(ONE_THOUSAND));
        department.addAtm(atm);
        assertEquals(ONE_THOUSAND.getDenomination(), department.getTotalBalance());
    }

    @Test
    public void addSomeAtmsAndReturnTotalBalance() {
        atm.putCash(new Cash(List.of(ONE_THOUSAND, SEVEN_HUNDRED)));
        department.addAtm(atm);
        final Atm newAtm = new Atm(new MinimumBanknotesQuantity());
        newAtm.putCash(new Cash(List.of(ONE_THOUSAND)));
        department.addAtm(newAtm);
        assertEquals(2_700, department.getTotalBalance());
    }
}