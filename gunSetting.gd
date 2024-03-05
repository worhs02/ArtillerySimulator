extends KinematicBody

# 발사체 이동 속도
var speed = 100

# 발사체 이동 방향
var direction = Vector3()

# 발사체의 초기 위치와 방향 설정
func init_translation_and_direction(initial_position: Vector3, target_position: Vector3):
	translation = initial_position
	direction = (target_position - initial_position).normalized()

# 발사체 이동 함수
func move(delta):
	var velocity = direction * speed
	move_and_slide(velocity)
