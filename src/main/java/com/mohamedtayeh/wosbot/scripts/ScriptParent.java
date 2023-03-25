package com.mohamedtayeh.wosbot.scripts;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptParent implements Runnable {

  public void run() {
    log.info("[call] " + this.getClass().getName());
  }
}
