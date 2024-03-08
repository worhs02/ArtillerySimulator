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
func _physics_process(delta):
	var velocity = direction * speed * delta
	move_and_slide(velocity)

func _ready():
	# 발사체 모델을 가져옴 (예시에서는 "res://path_to_model.obj"라고 가정)
	var mesh_instance = MeshInstance.new()
	var mesh = load("res://path_to_model.obj")  # 모델 로드
	mesh_instance.mesh = mesh  # 모델을 MeshInstance에 설정

	# 모델의 머티리얼에 쉐이더를 설정 (예시에서는 "res://path_to_shader.shader"라고 가정)
	var shader = load("RigidBody3D")  # 쉐이더 로드
	var material = SpatialMaterial.new()
	material.shader = shader  # 쉐이더를 머티리얼에 설정
	mesh_instance.material_override = material  # 머티리얼을 모델에 설정

	# 발사체 모델을 발사체에 추가
	add_child(mesh_instance)
