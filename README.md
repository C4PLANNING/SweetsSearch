# SweetsSearch

## What's this?
ジオフェンスの機能を活用した日常のちょっとしたアプリ。<br><br>
お店を登録しておくと、そのお店に近づいたときにリマインドしてくれます。<br><br>
スイーツだけじゃなく、リマインドしてほしい場所ならどこでも登録できます。<br><br>

## ジオフェンスとは
特定範囲の仮想境界線のことで、例えば弊社から500mをジオフェンスとしてマップに表示するとこんな感じの円になります。<br>
<img width="300" alt="geofence_500" src="https://user-images.githubusercontent.com/45345968/49057263-20fcc580-f242-11e8-8ed7-b0b5c995c5a6.png"><br><br>
この範囲を入ったときに通知する機能がジオフェンス機能になります。<br><br>

## 概要
ジョギングやウォーキング、日常の移動の際にあらかじめ登録していた店舗の周囲に近づいたときに、
身につけたAndroidWear（スマートウォッチ）が振動し知らせてくれるアプリです。<br><br>
## 特長
スマートウォッチと相性の良いGPS機能を用いたアプリです。
自分の好みのジャンルの店舗を登録（最大100地点）しておけばリマインド機能として活用できます。<br><br>
## 使い方
### 事前準備
1. GoogleMapからリマインド機能として登録しておきたい場所、施設を選択します。<br><br>
2. 選択した画面から、共有をタップします。<br><br>
3. add Geofenceをタップします。<br>
<img width="500" alt="addGeofence" src="https://user-images.githubusercontent.com/45345968/49060771-45f93480-f252-11e8-9305-db5f6688052a.png"><br><br>
4. ADD GEOFENCEをタップし、場所を追加します。<br>
<img width="310" alt="addGeofence2" src="https://user-images.githubusercontent.com/45345968/50759549-a7c4a980-12a8-11e9-93b4-63ea7aa32211.png"><br><br>

### 使用時
1. AndroidWearとスマホをペアリングしておけば、登録した施設から設定した半径内に到達した時に、
AndroidWearが振動し、お店情報を伝えてくれます。<br><img width="310" alt="addGeofence3" src="https://user-images.githubusercontent.com/45345968/50759945-c9726080-12a9-11e9-8802-c92f37f18745.png"><br><br>
2.AndroidWearのGoogleMapでお店の場所を確かめます。<br><img width="310" alt="addGeofence4" src="https://user-images.githubusercontent.com/45345968/50759956-cb3c2400-12a9-11e9-8f10-07569885d985.png"><br><br>

## ジオフェンス登録について
- GoogleMapの共有用短縮URLからGETリクエストしてリダイレクト先を取得します。<br><br>
- リダイレクト先のcidパラメータを取得します。<br><br>
- cidパラメータとGoogle Maps API (Places API)を使って https://maps.googleapis.com/maps/api/place/details/json?cid={cid}&key={APIKey} にGETリクエストを送ります。<br><br>
- json内のlat(緯度)とlng(経度)を取得します。
