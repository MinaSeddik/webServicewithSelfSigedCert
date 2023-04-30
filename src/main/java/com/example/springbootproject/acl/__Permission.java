package com.example.springbootproject.acl;

public enum __Permission {

    VIEW_ACCOUNT_DISCOUNT("View Account Discount"),
    VIEW_AND_ADD_ACCOUNT_DISCOUNT("View And Add Account Discount"),
    VIEW_AND_EDIT_ACCOUNT_DISCOUNT("View And Edit Account Discount"),
    VIEW_AND_DELETE_ACCOUNT_DISCOUNT("View And Delete Account Discount"),


    ASSIGN_CUSTOMER("Assign Customer"),
    VIEW_CUSTOMER_ATTACHMENTS("View Customer Attachments"),
    EDIT_CUSTOMER_ATTACHMENTS("Edit Customer Attachments"),
    DOWNLOAD_OR_EMAIL_ACCOUNT_NOTICE("Download or Email Account Notice"),
    VIEW_CHARGE_ACCOUNT_SECURE_URL("View Charge-Account Secure URL"),
    VIEW_CUSTOMERS("View Customers"),
    VIEW_AND_ADD_CUSTOMERS("View And Add Customers"),
    VIEW_AND_EDIT_CUSTOMERS("View And Edit Customers"),
    VIEW_AND_DELETE_CUSTOMERS("View And Delete Customers"),
    VIEW_AND_EXPORT_CUSTOMERS("View And Export Customers"),


    VIEW_DIRECT_DEPOSIT("View Direct Deposit"),
    EDIT_DELETE_DIRECT_DEPOSIT("Edit/Delete Direct Deposit"),


    SEND_INVOICES_EMAILS("Send Invoices E-Mails"),
    EDIT_INVOICE_DETAILS("Edit Invoice Details"),
    EDIT_INVOICE_PAYMENTS("Edit Invoice Payments"),
    EDIT_EMAIL_CONTENT_AND_ATTACHMENTS("Edit Email Content And Attachments"),
    ADD_MANUAL_INVOICE("Add Manual Invoice"),
    INVOICE_CREATION("Invoice Creation"),
    VIEW_AND_EXPORT_INVOICES("View And Export Invoices"),


    VIEW_USERS("View Users"),
    VIEW_AND_ADD_USERS("View And Add Users"),
    VIEW_AND_EDIT_USERS("View And Edit Users"),
    VIEW_AND_DELETE_USERS("View And Delete Users"),
    ACTIVATE_DEACTIVATE_USERS("Activate/Deactivate users"),
    EDIT_MY_PROFILE("Edit My Profile"),


    VIEW_ROLES("View Roles"),
    VIEW_AND_ADD_ROLES("View And Add Roles"),
    VIEW_AND_EDIT_ROLES("View And Edit Roles"),
    VIEW_AND_DELETE_ROLES("View And Delete Roles");

    private final String name;

    __Permission(String name) {
        this.name = name;
    }

    public static __Permission resolvePermissionName(String text) {
        for (__Permission permission : __Permission.values()) {
            if (permission.name.equalsIgnoreCase(text)) {
                return permission;
            }
        }
        return null;
    }

}
