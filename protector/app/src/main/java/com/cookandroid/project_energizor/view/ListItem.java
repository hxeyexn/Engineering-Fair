package com.cookandroid.project_energizor.view;

public class ListItem {

    private String name;
    private String sex;
    private String age;
    private String position;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    ListItem(String name, String sex, String age, String position, String description) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.position = position;
        this.description = description;
    }
}
