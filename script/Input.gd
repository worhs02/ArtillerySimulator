extends Node

var button: Button 
var line_editX: LineEdit
var line_editY: LineEdit

# Called when the node enters the scene tree for the first time.
func _ready():
	button = $start/finish 
	line_editX = $start/위도
	line_editY = $start/경도
	button.pressed.connect(_on_button_pressed)


# 버튼이 클릭되었을 때 실행되는 함수
func _on_button_pressed():
	var x = line_editX.text
	var y = line_editY.text
	print(x)
	print(y)
	# 이곳에 버튼 클릭 시 수행할 동작을 추가합니다.
	
