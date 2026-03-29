"""Route registration regression tests."""


def test_client_route_is_registered(client):
    response = client.options("/client/v1/plans")
    assert response.status_code != 404


def test_admin_route_is_registered(client):
    response = client.get("/admin/v1/orders")
    assert response.status_code in {401, 403}


def test_openapi_includes_client_and_admin_paths(client):
    response = client.get("/openapi.json")
    assert response.status_code == 200

    paths = response.json().get("paths", {})
    assert "/client/v1/plans" in paths
    assert "/admin/v1/orders" in paths
