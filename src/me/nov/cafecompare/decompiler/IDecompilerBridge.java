package me.nov.cafecompare.decompiler;

public interface IDecompilerBridge {
  void setAggressive(boolean aggressive);

  String decompile(String name, byte[] bytes);
}
