'use strict';

import Util from '../utils/Util';

export default {

    saveForm(config) {
        return Util.ajax({
            url: '/form/saveForm.json',
            ...config
        });
    },

    getForm(config) {
        return Util.ajax({
            url: '/form/getForm.json',
            ...config
        });
    },

    register(config) {
        return Util.ajax({
            url: '/form/register.json',
            ...config
        });
    },

    getMyFormList(config) {
        return Util.ajax({
            url: '/form/getMyFormList.json',
            ...config
        });
    },

    getAttendedActivityList(config) {
        return Util.ajax({
            url: '/form/getAttendedActivityList.json',
            ...config
        });
    }

};
