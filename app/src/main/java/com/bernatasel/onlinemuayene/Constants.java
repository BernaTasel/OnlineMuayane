package com.bernatasel.onlinemuayene;

public class Constants {
    public static boolean IS_DEBUG = false;

    public enum STATUS {
        ONLINE("Çevrimiçi", 1), BUSY("Meşgul", 2), ONLYMSG("Sadece Mesaj", 3), OFFLINE("Çevrimdışı", 4);
        private final String name;
        private final int value;

        STATUS(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public static STATUS getByValue(int value) {
            for (STATUS status : STATUS.values())
                if (status.getValue() == value) return status;
            return null;
        }
    }

    public enum GENDER {
        FEMALE("female", "Kadın"), MALE("male", "Erkek");

        private final String name, description;

        GENDER(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum USER_TYPE {
        ADMIN("admin", "Admin"), DOCTOR("doctor", "Doktor"), PATIENT("patient", "Hasta");

        private final String name, description;

        USER_TYPE(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public static USER_TYPE getByName(String name) {
            for (USER_TYPE userType : USER_TYPE.values())
                if (userType.name.equals(name)) return userType;
            return null;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
