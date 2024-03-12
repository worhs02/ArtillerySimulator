extends Sprite2D

var origin_latitude = 0 # 출발 지점 위도
var origin_longitude = 0 # 출발 지점 경도
var destination_latitude = 0 # 도착 지점 위도
var destination_longitude = 0 # 도착 지점 경도

var naver_map_api_key = "y0wyj0cbep" # 네이버 지도 API 키 입력
var map_view_markers = ""

func _ready():
	
	# 네이버 지도 뷰 추가
	var map_view_url = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?w=800&h=600"
	var map_view_params = "&level=10"
	
	var request_url = map_view_url + "&center=" + str(origin_longitude) + "," + str(origin_latitude) + map_view_params + map_view_markers + "&key=" + naver_map_api_key
	load_request(request_url)

func get_coordinate(x,y):
	
	# 출발 지점과 도착 지점 마커 추가
	add_marker(origin_latitude, origin_longitude)
	destination_latitude = x # 도착 지점 위도
	destination_longitude = y 
	add_marker(destination_latitude, destination_longitude)

	# 출발 지점과 도착 지점 사이의 직선 거리를 라인으로 연결
	draw_line(Vector2(origin_longitude, origin_latitude), Vector2(destination_longitude, destination_latitude), Color.BLUE, 2.0)
	
	

func add_marker(latitude, longitude):
	var marker = "&markers=type:t|size:mid|pos:" + str(longitude) + "," + str(latitude)
	map_view_markers += marker

func draw_custom_line(start_point, end_point, color, width):
	var line = "&path=2," + str(color.to_html()) + "," + str(width) + ",," + str(start_point.x) + "%20" + str(start_point.y) + "," + str(end_point.x) + "%20" + str(end_point.y)
	map_view_markers += line


func load_request(url):
	var http_request = HTTPRequest.new()
	http_request.request_completed.connect(_on_http_request_request_completed)
	http_request.request(url)


func _on_http_request_request_completed(result, response_code, headers, body):
	if response_code == 200:
		# 요청이 성공했을 때의 로직을 여기에 추가합니다.
		var map_texture = ImageTexture.new()
		map_texture.load_png_from_buffer(body)
		texture = map_texture

	else:
		# 요청이 실패했을 때의 로직을 여기에 추가합니다.
		print("API 요청이 실패했습니다. 응답 코드:", response_code)
