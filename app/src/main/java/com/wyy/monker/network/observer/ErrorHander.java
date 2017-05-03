package com.wyy.monker.network.observer;

import android.content.Context;

/**
 * Created by Administrator on 2017/4/28.
 */

public class ErrorHander {

    public ErrorHander(Context context,String status, String message) {
        handError(context,status, message);
    }

    private void handError(Context context,String status, String message) {
//        ToastUtil.show(message);
//        switch (status) {
//            case DMConst.PublicStatus.ERROR:
//                break;
//            case DMConst.PublicStatus.NORMAL:
//
//                break;
//            case DMConst.PublicStatus.NULLRETURN:
//
//                break;
//            case DMConst.PublicStatus.AUTHENTICATION_NOT_COMPLETED:
//
//                break;
//            case DMConst.PublicStatus.REAL_NAME_UNFINISHED:
//
//                break;
//            case DMConst.PublicStatus.BANK_REAL_NAME_UNFINISHED:
//
//                break;
//            case DMConst.PublicStatus.TOKEN_EXPIRE:
//
//                break;
//            case DMConst.PublicStatus.NO_LOGIN:
//                DMApplication.clearMySelfUser();
//                context.startActivity(LoginRegisterActivity.newIntent(context, DMNetError.RELOGIN));
//                break;
//            case DMConst.PublicStatus.SPECIAL:
//
//                break;

//        }
    }
}
