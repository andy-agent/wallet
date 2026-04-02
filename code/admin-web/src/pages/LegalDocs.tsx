import { Card, Empty } from 'antd';

const LegalDocs = () => {
  return (
    <div>
      <h2 style={{ marginBottom: 24 }}>法务文档</h2>
      <Card>
        <Empty description="法务文档管理骨架已就位，待接入真实 Legal Document API" />
      </Card>
    </div>
  );
};

export default LegalDocs;
