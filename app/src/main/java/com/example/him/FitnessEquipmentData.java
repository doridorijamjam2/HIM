package com.example.him;

import java.util.ArrayList;

public class FitnessEquipmentData {
    private volatile static FitnessEquipmentData _instance = null;

    private ArrayList<FitnessEquipmentItem> items;

    /* 싱글톤 패턴 적용 */
    public static FitnessEquipmentData getInstance() {
        if (_instance == null) {
            synchronized (FitnessEquipmentData.class) {
                if (_instance == null) {
                    _instance = new FitnessEquipmentData();
                }
            }
        }

        return _instance;
    }

    private FitnessEquipmentData() {
        // 초기화 (데이터 생성)
        init();
    }

    /* 초기화 (데이터 생성) */
    private void init() {
        this.items = new ArrayList<>();

        // 가슴
        this.items.add(new FitnessEquipmentItem(1, 1, "벤치프레스", "https://youtu.be/o6jUa3sQQFw"));
        this.items.add(new FitnessEquipmentItem(2, 1, "인클라인 벤치프레스", "https://youtu.be/4HvI_mFhzVQ"));
        this.items.add(new FitnessEquipmentItem(3, 1, "딥스", "https://youtu.be/pQSfXvaQGas"));
        this.items.add(new FitnessEquipmentItem(4, 1, "케이블 크로스 오버", "https://youtu.be/Il7FXbxd1cY"));

        // 하체
        this.items.add(new FitnessEquipmentItem(5, 2, "레그컬", "https://youtu.be/5qopt3rsFhA"));
        this.items.add(new FitnessEquipmentItem(6, 2, "어덕션", "https://youtu.be/JbKtKZwlGVA"));
        this.items.add(new FitnessEquipmentItem(7, 2, "카프레이즈", "https://youtu.be/Y6IU0Rgg1lE"));
        this.items.add(new FitnessEquipmentItem(8, 2, "레그프레스", "https://youtu.be/RLgvLK14vpc"));

        // 등
        this.items.add(new FitnessEquipmentItem(9, 3, "데드리프트", "https://www.youtube.com/watch?v=AqcS0YLCMaU&list=PLeW64XvUU8ogLuV48Agdphy397ICXshSI&index=6"));
        this.items.add(new FitnessEquipmentItem(10, 3, "랫풀다운", "https://www.youtube.com/watch?v=m3547euAcSU&list=PLeW64XvUU8ogDzb4ZaKEGgln5l_2eBF0f&index=16"));
        this.items.add(new FitnessEquipmentItem(11, 3, "어시스트 풀업", "https://www.youtube.com/watch?v=nfORs3t0G44&list=PLeW64XvUU8ogDzb4ZaKEGgln5l_2eBF0f&index=15"));
        this.items.add(new FitnessEquipmentItem(12, 3, "롱풀", "https://www.youtube.com/watch?v=1Bv9zDj9mg8&list=PLeW64XvUU8ogDzb4ZaKEGgln5l_2eBF0f&index=7"));

        // 어깨
        this.items.add(new FitnessEquipmentItem(13, 4, "숄더프레스", "https://youtube.com/watch?v=YIYXTvL5oqM&feature=share"));
        this.items.add(new FitnessEquipmentItem(14, 4, "렛풀다운", "https://youtube.com/watch?v=SablLM0l8RA&feature=share"));
        this.items.add(new FitnessEquipmentItem(15, 4, "어퍼백", "https://youtube.com/watch?v=h6zLSYqqjfU&feature=share"));
        this.items.add(new FitnessEquipmentItem(16, 4, "밀리터리프레스", "https://youtube.com/watch?v=LIBdj3PLpcI&feature=share"));
    }

    /* 데이터 얻기 */
    public ArrayList<FitnessEquipmentItem> getItems(int partKind) {
        ArrayList<FitnessEquipmentItem> data = new ArrayList<>();

        for (FitnessEquipmentItem item : this.items) {
            if (item.partKind == partKind) {
                data.add( item);
            }
        }

        return data;
    }
}
