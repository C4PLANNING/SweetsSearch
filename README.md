# SweetsSearch

## What's this?
ジオフェンスの機能を活用した日常のちょっとしたアプリ<br><br>
お店を登録しておくと、そのお店に近づいたときにリマインドしてくれます。<br><br>
スイーツだけじゃなく、リマインドしてほしい場所ならどこでも登録できます。<br><br>

## ジオフェンスとは
特定範囲の仮想境界線のことで、例えば弊社から500mをジオフェンスとしてマップに表示するとこんな感じの円になります。
<img width="300" alt="geofence_500" src="https://user-images.githubusercontent.com/45345968/49057263-20fcc580-f242-11e8-8ed7-b0b5c995c5a6.png"><br><br>
この範囲から入ったりしたときに通知する機能がジオフェンス機能になります。<br><br>

## 概要
ジョギングやウォーキング、日常の移動の際にあらかじめ登録していた店舗の周囲に近づいたときに、
身につけたAndroidWear（スマートウォッチ）が振動し知らせてくれるアプリです。<br><br>
## 特長
スマートウォッチと相性の良いGPS機能を用いたアプリです。
自分の好みのジャンルの店舗を登録しておけばリマインド機能として活用できます。<br><br>
## 使い方
1. GoogleMapからリマインド機能として登録しておきたい場所、施設を選択します。<br><br>
2. 選択した画面から、共有をタップします。<br><br>
3. add Geofenceをタップします。
<img width="300" alt="geofence_500" src="https://user-images.githubusercontent.com/45345968/49057263-20fcc580-f242-11e8-8ed7-b0b5c995c5a6.png"><br><br>
4. ADD GEOFENCEをタップし、場所を追加します。
<img width="300" alt="geofence_500" src="https://user-images.githubusercontent.com/45345968/49057263-20fcc580-f242-11e8-8ed7-b0b5c995c5a6.png"><br><br>
5. AndroidWearとスマホをペアリングしておけば、登録した施設から設定した半径内に到達した時に、
AndroidWearが振動し、お店情報を伝えてくれます。<br>
## ジオフェンス登録について
- GoogleMapの共有用短縮URLからHEADリクエストしてlocationヘッダからリダイレクト先を取得します。<br><br>
- リダイレクト先のcidパラメータを取得します。<br><br>
- cidパラメータを使って http://maps.google.com/maps?cid=#{cid}&hl=ja&output=json にGETリクエストを送ります。<br><br>
- centerプロパティ内のlat(緯度)とlng(経度)を取得します。
