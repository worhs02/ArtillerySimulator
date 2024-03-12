extends Button

@onready var http_request = $HTTPRequest
const URL = "https://naveropenapi.apigw.ntruss.com/map-{service}/{version}/{operation}?{parameters}
"
# Called when the node enters the scene tree for the first time.



func _on_pressed():
	http_request.request(URL)


func _on_http_request_request_completed(result, response_code, headers, body):
	pass # Replace with function body.
