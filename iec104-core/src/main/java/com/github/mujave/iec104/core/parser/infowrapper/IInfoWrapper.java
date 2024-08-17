package com.github.mujave.iec104.core.parser.infowrapper;

import com.github.mujave.iec104.core.parser.frame.ASDUFrame;

/**
 *
 * @author 张雨
 */
public interface IInfoWrapper {


    void wrapper(ASDUFrame res,byte[] msg);
}
