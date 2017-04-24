'use strict';

import AppDataActionTypes from '../../components/AppData/AppDataActionTypes';
import CustomFormActionTypes from '../../pages/CustomForm/CustomFormActionTypes';

const {_} = window._external;

const ActionTypes = {

    ...AppDataActionTypes,
    ...CustomFormActionTypes

};

_.forEach(ActionTypes, (value, key) => {
    _.assign(value, {
        changeState: `${key}.changeState`,
        replaceState: `${key}.replaceState`
    });
});

export default ActionTypes;
