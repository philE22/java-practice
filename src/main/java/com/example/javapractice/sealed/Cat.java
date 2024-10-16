package com.example.javapractice.sealed;

public sealed class Cat implements Animal permits CatImpl {    // sealed는 다른 subclass를 가져야함

}
