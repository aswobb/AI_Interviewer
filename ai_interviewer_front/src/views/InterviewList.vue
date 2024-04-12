<template>
    <div>
        <v-data-table :headers="headers" :items="desserts" item-key="name" editable>
            <template v-slot:item="{ item }">
                <tr>
                    <td>
                        <v-edit-dialog :return-value.sync="item.name">
                            {{ item.name }}
                            <template v-slot:input>
                                <v-text-field v-model="item.name" label="Edit"></v-text-field>
                            </template>
                        </v-edit-dialog>
                    </td>
                    <td>
                        <v-edit-dialog :return-value.sync="item.calories">
                            {{ item.calories }}
                            <template v-slot:input>
                                <v-text-field v-model="item.calories" label="Edit"></v-text-field>
                            </template>
                        </v-edit-dialog>
                    </td>
                    <!-- 其他列 -->
                </tr>
            </template>

        </v-data-table>
        <v-sheet class="d-flex justify-center align-center" style="height: 100px;">
            <v-btn color="primary">按钮</v-btn>
        </v-sheet>
    </div>
</template>

<style></style>

<script>
export default {
    data() {
        return {
            headers: [
                { text: 'Dessert (100g serving)', value: 'name' },
                { text: 'Calories', value: 'calories' },
                // 其他列数据
            ],
            desserts: [
                // 数据列表
                {
                    name: 'Frozen Yogurt',
                    calories: 159,
                    fat: 6.0,
                    carbs: 24,
                    protein: 4.0,
                    iron: 1,
                },
                {
                    name: 'Ice cream sandwich',
                    calories: 237,
                    fat: 9.0,
                    carbs: 37,
                    protein: 4.3,
                    iron: 1,
                },
                {
                    name: 'Eclair',
                    calories: 262,
                    fat: 16.0,
                    carbs: 23,
                    protein: 6.0,
                    iron: 7,
                },
                {
                    name: 'Cupcake',
                    calories: 305,
                    fat: 3.7,
                    carbs: 67,
                    protein: 4.3,
                    iron: 8,
                },
                {
                    name: 'Gingerbread',
                    calories: 356,
                    fat: 16.0,
                    carbs: 49,
                    protein: 3.9,
                    iron: 16,
                },
                {
                    name: 'Jelly bean',
                    calories: 375,
                    fat: 0.0,
                    carbs: 94,
                    protein: 0.0,
                    iron: 0,
                },
                {
                    name: 'Lollipop',
                    calories: 392,
                    fat: 0.2,
                    carbs: 98,
                    protein: 0,
                    iron: 2,
                },
                {
                    name: 'Honeycomb',
                    calories: 408,
                    fat: 3.2,
                    carbs: 87,
                    protein: 6.5,
                    iron: 45,
                },
                {
                    name: 'Donut',
                    calories: 452,
                    fat: 25.0,
                    carbs: 51,
                    protein: 4.9,
                    iron: 22,
                },
                {
                    name: 'KitKat',
                    calories: 518,
                    fat: 26.0,
                    carbs: 65,
                    protein: 7,
                    iron: 6,
                },

            ]
        };
    }
};
</script>