extends Sprite2D

var origin_latitude = 35 # 출발 지점 위도
var origin_longitude = 129 # 출발 지점 경도
var destination_latitude = 0 # 도착 지점 위도
var destination_longitude = 0 # 도착 지점 경도

var naver_map_api_key = "y0wyj0cbep" # 네이버 지도 API 키 입력
var map_view_markers = ""

func _ready():
	# 출발 지점과 도착 지점 마커 추가
	
	# 요청 URL 생성
	var map_view_url = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?w=800&h=600"
	var map_view_params = "&level=10"
	var request_url = map_view_url + "&center=" + str(origin_longitude) + "," + str(origin_latitude) + map_view_params + map_view_markers + "&key=" + naver_map_api_key
	# API 요청
	load_request(request_url)
	add_marker(origin_latitude, origin_longitude)
	# 도착 지점 설정
	get_coordinate(37, 127)  # 예시로 좌표 설정

func get_coordinate(x, y):
	destination_latitude = x
	destination_longitude = y 
	add_marker(destination_latitude, destination_longitude)
	

func add_marker(latitude, longitude):
	var marker = "&markers=type:t|size:mid|pos:" + str(longitude) + "," + str(latitude)
	map_view_markers += marker

func draw_custom_line(start_point, end_point, color, width):
	var line = "&path=2," + str(color.to_html()) + "," + str(width) + ",," + str(start_point.x) + "%20" + str(start_point.y) + "," + str(end_point.x) + "%20" + str(end_point.y)
	map_view_markers += line

func load_request(url):
	var http_request = HTTPRequest.new()
	 # HTTPRequest 객체를 씬에 추가
	add_child(http_request)
	http_request.request_completed.connect(_on_http_request_request_completed)
	http_request.request(url)

func _on_http_request_request_completed(result, response_code, headers, body):
	if response_code == 200:
		var map_texture = ImageTexture.new()
		map_texture.load_png_from_buffer(body)
		texture = map_texture
	else:
		print("API 요청이 실패했습니다. 응답 코드:", response_code)
func _draw():
	# 출발 지점과 도착 지점 사이의 직선을 그립니다.
	draw_line(Vector2(origin_longitude, origin_latitude), Vector2(destination_longitude, destination_latitude), Color.BLUE, 2.0)
