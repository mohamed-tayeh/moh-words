package com.mohamedtayeh.wosbot.scripts;

import com.mohamedtayeh.wosbot.features.dictionaryApi.DictionaryApi;
import org.springframework.stereotype.Service;

@Service
public class CallDictionaryApi implements Script {

  private final DictionaryApi dictionaryApi;

  public CallDictionaryApi(DictionaryApi dictionaryApi) {
    this.dictionaryApi = dictionaryApi;
  }

  @Override
  public void run() {

    System.out.println(dictionaryApi.isWord("asnfsaf"));
  }
}
