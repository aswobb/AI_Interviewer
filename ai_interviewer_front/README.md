- [AI面接官](#ai面接官)
  - [プロジェクトのセットアップ](#プロジェクトのセットアップ)
    - [開発用のコンパイルとホットリロード](#開発用のコンパイルとホットリロード)
    - [実稼働用にコンパイル](#実稼働用にコンパイル)
  - [注意点](#注意点)
    - [DBについて](#dbについて)
  - [使用方法](#使用方法)
    - [1. ログイン](#1-ログイン)
    - [2. 面接開始](#2-面接開始)
    - [3. 面接](#3-面接)
    - [4. 面接終了](#4-面接終了)

# AI面接官

## プロジェクトのセットアップ
```
npm install
```

### 開発用のコンパイルとホットリロード
```
npm run serve
```

### 実稼働用にコンパイル
```
npm run build
```
---
## 注意点
### DBについて  
社内チャットボットと同じDBを使用しています。(ユーザ表、GPT設定表)  


---

## 使用方法
### 1. ログイン
社内チャットボットと同じID/Passでログイン

### 2. 面接開始
2-1. 面接で重視したいポイントをチェックボックスで選択し、送信ボタンを押下  
(送信メッセージは自動生成されます。)
2-2. スキルシートを提出するよう促されるはずなので、スキルシートを貼り付けて送信。

### 3. 面接
ChatGPTから1つずつ質問が来るので、それに回答していく。  
質問に5問答えるたびに、面接を続けるか終了するか聞かれるので、終了したければ終了する旨を、続けたければ続ける旨を送信する。

### 4. 面接終了
「以上で面接を終了します」という文言を検知した場合、面接を終了し、これまでの質問と回答をCSV形式で出力する。  
面接終了後はそれ以上の入力ができないようにしている。
