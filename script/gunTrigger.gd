extends Node

# 발사체 스크립트 파일 경로
const PROJECTILE_SCRIPT_PATH = "res://gunSetting.gd"

func _ready():
	pass

# 사용자가 채팅창에 좌표를 입력했을 때 호출되는 함수
func on_coordinates_received(x, y, z):
	var target_position = Vector3(float(x), float(y), float(z))
	shoot(target_position)

func shoot(target_position: Vector3):
	# 발사체 노드를 인스턴스화합니다.
	var projectile = preload(PROJECTILE_SCRIPT_PATH).instance()
	
	# 발사체 노드를 씬에 추가합니다. 이 예제에서는 부모 노드에 직접 추가합니다.
	add_child(projectile)
	
	# 발사체를 초기 위치로 이동시킵니다. (여기서는 Main 노드의 위치를 사용하겠습니다.)
	projectile.translation = global_transform.origin
	
	# 발사체의 초기 위치와 방향 설정
	projectile.init_translation_and_direction(global_transform.origin, target_position)
