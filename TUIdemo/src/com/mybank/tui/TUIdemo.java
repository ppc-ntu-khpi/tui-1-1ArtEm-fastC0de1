package com.mybank.tui;

import jexer.TAction;
import jexer.TApplication;
import jexer.TField;
import jexer.TText;
import jexer.TWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;


import com.mybank.domain.Account;
import com.mybank.domain.Bank;
import com.mybank.domain.CheckingAccount;
import com.mybank.domain.Customer;
import com.mybank.domain.SavingsAccount;

/**
 *
 * @author Alexander 'Taurus' Babich
 */
public class TUIdemo extends TApplication {

    private static final int ABOUT_APP = 2000;
    private static final int CUST_INFO = 2010;

    public static void main(String[] args) throws Exception {
        TUIdemo tdemo = new TUIdemo();
        (new Thread(tdemo)).start();
    }

    public TUIdemo() throws Exception {
        super(BackendType.SWING);

        addToolMenu();
        //custom 'File' menu
        TMenu fileMenu = addMenu("&File");
        fileMenu.addItem(CUST_INFO, "&Customer Info");
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
        //end of 'File' menu  

        addWindowMenu();

        //custom 'Help' menu
        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(ABOUT_APP, "&About...");
        //end of 'Help' menu 

        setFocusFollowsMouse(true);
        //Customer window
        ShowCustomerDetails();
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        if (menu.getId() == ABOUT_APP) {
            messageBox("About", "\t\t\t\t\t   Just a simple Jexer demo.\n\nCopyright \u00A9 2019 Alexander \'Taurus\' Babich").show();
            return true;
        }
        if (menu.getId() == CUST_INFO) {
            ShowCustomerDetails();
            return true;
        }
        return super.onMenu(menu);
    }

    private void ShowCustomerDetails() {

    // ---------- Створення тестових даних ----------
    Bank bank = Bank.getBank();

    // Щоб клієнти не дублювались при повторному відкритті вікна
    if (Bank.getNumberOfCustomers() == 0) {

        // Клієнт 0
        Bank.addCustomer("John", "Doe");

        Customer c1 = Bank.getCustomer(0);
        c1.addAccount(
            new CheckingAccount(1500.0, 500.0)
        );

        // Клієнт 1
        Bank.addCustomer("Alice", "Smith");

        Customer c2 = Bank.getCustomer(1);
        c2.addAccount(
            new SavingsAccount(3200.0, 0.05)
        );
    }

    // ---------- Вікно ----------
    TWindow custWin = addWindow(
        "Customer Window",
        2, 1,
        55, 14,
        TWindow.NOZOOMBOX
    );

    custWin.newStatusBar(
        "Enter customer number and press Show..."
    );

    custWin.addLabel(
        "Enter customer number: ",
        2, 2
    );

    TField custNo = custWin.addField(
        26, 2,
        5,
        false
    );

    TText details = custWin.addText(
        "Customer information will appear here...",
        2, 5,
        50,
        7
    );

    // ---------- Кнопка ----------
    custWin.addButton("&Show", 35, 2, new TAction() {

        @Override
        public void DO() {

            try {

                int custNum =
                    Integer.parseInt(custNo.getText());

                // Перевірка номера клієнта
                if (custNum < 0 ||
                    custNum >= Bank.getNumberOfCustomers()) {

                    messageBox(
                        "Error",
                        "Customer not found!"
                    ).show();

                    return;
                }

                Customer customer =
                    Bank.getCustomer(custNum);

                Account account =
                    customer.getAccount(0);

                // Визначення типу рахунку
                String accType = "Unknown";

                if (account instanceof CheckingAccount) {
                    accType = "Checking";
                }

                if (account instanceof SavingsAccount) {
                    accType = "Savings";
                }

                // Формування тексту
                String info =
                    "Customer ID: " + custNum + "\n"
                    + "First Name: "
                    + customer.getFirstName() + "\n"
                    + "Last Name: "
                    + customer.getLastName() + "\n"
                    + "Account Type: "
                    + accType + "\n"
                    + "Balance: $"
                    + account.getBalance();

                details.setText(info);

            } catch (NumberFormatException e) {

                messageBox(
                    "Error",
                    "Invalid customer number!"
                ).show();

            } catch (Exception e) {

                messageBox(
                    "Error",
                    e.getMessage()
                ).show();
            }
        }
    });
}
}
