import { Card, Empty } from 'antd';

const Versions = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>版本管理</h2>
      <Card>
        <Empty description="版本管理骨架已就位，待接入真实 Version API" />
      </Card>
    </div>
  );
};

export default Versions;
