extends Node

var button: Button 
var line_editX: LineEdit
var line_editY: LineEdit
signal value_changed(x, y)
var get_coordinate = preload("res://naverApi.gd")

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
	emit_signal("value_changed", x, y)
	print(x)
	print(y)
	var get_coordinate_instance = get_coordinate.new()
	get_coordinate_instance.get_coordinate(x,y)
	get_tree().change_scene_to_file("res://scene/aerialShot.tscn")
	# 이곳에 버튼 클릭 시 수행할 동작을 추가합니다.
	


func _on_finish_pressed():
	pass
