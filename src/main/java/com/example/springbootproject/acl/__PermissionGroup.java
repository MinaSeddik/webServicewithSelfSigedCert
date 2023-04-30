package com.example.springbootproject.acl;

import java.util.EnumSet;

import static com.example.springbootproject.acl.__Permission.*;

public class __PermissionGroup {

    private EnumSet<__Permission> accountVolumeDiscounts = EnumSet.of(VIEW_ACCOUNT_DISCOUNT,
            VIEW_AND_ADD_ACCOUNT_DISCOUNT,
            VIEW_AND_EDIT_ACCOUNT_DISCOUNT,
            VIEW_AND_DELETE_ACCOUNT_DISCOUNT);



}
